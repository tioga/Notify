package org.tiogasolutions.notify.sender.http;

import org.tiogasolutions.notify.notifier.send.SendNotificationRequest;
import org.tiogasolutions.notify.notifier.NotifierException;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequestJsonBuilder;
import org.tiogasolutions.notify.notifier.send.NotificationAttachment;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;
import org.tiogasolutions.notify.notifier.send.AbstractNotificationSender;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 1:10 AM
 */
public class HttpNotificationSender extends AbstractNotificationSender {
  private static final Logger log = LoggerFactory.getLogger(HttpNotificationSender.class);
  private final ExecutorService executorService;
  private final Client client;
  private final String baseUrl;

  public HttpNotificationSender(HttpNotificationSenderConfig config) {
    // Build the client
    Configuration httpClientConfig = new ClientConfig()
        .register(MultiPartFeature.class);

    ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(httpClientConfig);
    if (config.getSslConfig() != null) {
      // Using SSL set assign context.
      clientBuilder.sslContext(config.getSslConfig().getSSLContext());
    }
    client = clientBuilder.build();
    HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(config.getUserName(), config.getPassword());
    client.register(feature);
    baseUrl = config.getUrl();
    executorService = Executors.newSingleThreadExecutor();
    log.info("Configured HttpNotificationSender for " + baseUrl);
  }

  @Override
  public Future<SendNotificationResponse> send(SendNotificationRequest request) {
    // Never throw an exception from here.
    // TODO - make this async

    Callable<SendNotificationResponse> callable = () -> {
      Response sendResponse;
      try {
        // Send the request
        sendResponse = sendRequest(request, SendNotificationRequest.Status.SENDING);

      } catch (Exception t) {
        SendNotificationResponse notificationResponse = SendNotificationResponse.newFailure(request, t);
        callbacks.callFailure(notificationResponse);
        log.error("Failure sending notification request: ", t);
        return notificationResponse;
      }

      int status = sendResponse.getStatus();
      if (status == 200 || status == 201) {
        Link attachmentLink = sendResponse.getLink("attachments");
        for (NotificationAttachment attachment : request.getAttachments()) {
          try {
            sendAttachment(request, attachment, attachmentLink);
          } catch (Throwable t) {
            callbacks.callFailure(request, attachment, t);
            log.error("Failure sending notification attachments: ", t);
            return SendNotificationResponse.newFailure(request, t);
          }
        }

        // Request success
        SendNotificationResponse notificationResponse = SendNotificationResponse.newSuccess(request);
        callbacks.callSuccess(notificationResponse);
        return notificationResponse;

      } else {
        // Request failure
        NotifierException ex = new NotifierException("Non successful response from send: " + sendResponse.getStatus());
        SendNotificationResponse notificationResponse = SendNotificationResponse.newFailure(request, ex);
        this.callbacks.callFailure(notificationResponse);
        return notificationResponse;
      }
    };

    return executorService.submit(callable);

  }

  public void dispose() {
    executorService.shutdown();
  }

  protected Response sendRequest(SendNotificationRequest request, SendNotificationRequest.Status status) {

    String json = new SendNotificationRequestJsonBuilder().toJson(request, status);

    // Jersey does not allow entity value to be null.
    Entity entity = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
    WebTarget webTarget = client.target(baseUrl);

    return webTarget.request(MediaType.APPLICATION_JSON_TYPE)
        .header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
        .put(entity);
  }

  protected void sendAttachment(SendNotificationRequest request, NotificationAttachment attachment, Link attachmentLink) {
    StreamDataBodyPart streamPart = new StreamDataBodyPart(
        attachment.getName(),
        attachment.getInputStream(),
        attachment.getContentType()
    );

    WebTarget webTarget = client.target(attachmentLink)
        .path(attachment.getName());

    final MultiPart multipart = new FormDataMultiPart()
        .bodyPart(streamPart);

    Response response = webTarget.request()
        .post(Entity.entity(multipart, multipart.getMediaType()));

    if (response.getStatus() != 200 && response.getStatus() != 201) {
      String msg = String.format("Failure sending attachment %s", response.getStatusInfo().getReasonPhrase());
      callbacks.callFailure(request, attachment, new NotifierException(msg));
      log.error(msg);
    }
  }

  protected ExecutorService getExecutorService() {
    return executorService;
  }

  protected Client getClient() {
    return client;
  }

  protected String getBaseUrl() {
    return baseUrl;
  }
}
