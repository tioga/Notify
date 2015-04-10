package org.tiogasolutions.notify.sender.http;

import org.tiogasolutions.notify.notifier.request.LqRequest;
import org.tiogasolutions.notify.notifier.request.LqRequestStatus;
import org.tiogasolutions.notify.notifier.LqException;
import org.tiogasolutions.notify.notifier.json.LqRequestJsonBuilder;
import org.tiogasolutions.notify.notifier.request.LqAttachment;
import org.tiogasolutions.notify.notifier.request.LqResponse;
import org.tiogasolutions.notify.notifier.sender.LqAbstractSender;
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
public class LqHttpSender extends LqAbstractSender {
  private static final Logger log = LoggerFactory.getLogger(LqHttpSender.class);
  private final ExecutorService executorService;
  private final Client client;
  private final String baseUrl;

  public LqHttpSender(LqHttpSenderConfig config) {
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
    log.info("Configured LqHttpSender for " + baseUrl);
  }

  @Override
  public Future<LqResponse> send(LqRequest request) {
    // Never throw an exception from here.
    // TODO - make this async

    Callable<LqResponse> callable = () -> {
      Response sendResponse;
      try {
        // Send the request
        sendResponse = sendRequest(request, LqRequestStatus.SENDING);

      } catch (Exception t) {
        LqResponse lqResponse = LqResponse.newFailure(request, t);
        callbacks.callFailure(lqResponse);
        log.error("Failure sending Lq notification request: ", t);
        return lqResponse;
      }

      int status = sendResponse.getStatus();
      if (status == 200 || status == 201) {
        Link attachmentLink = sendResponse.getLink("attachments");
        for (LqAttachment attachment : request.getAttachments()) {
          try {
            sendAttachment(request, attachment, attachmentLink);
          } catch (Throwable t) {
            callbacks.callFailure(request, attachment, t);
            log.error("Failure sending Lq notification attachments: ", t);
            return LqResponse.newFailure(request, t);
          }
        }

        // Request success
        LqResponse lqResponse = LqResponse.newSuccess(request);
        callbacks.callSuccess(lqResponse);
        return lqResponse;

      } else {
        // Request failure
        LqException ex = new LqException("Non successful response from send: " + sendResponse.getStatus());
        LqResponse lqResponse = LqResponse.newFailure(request, ex);
        this.callbacks.callFailure(lqResponse);
        return lqResponse;
      }
    };

    return executorService.submit(callable);

  }

  public void dispose() {
    executorService.shutdown();
  }

  protected Response sendRequest(LqRequest request, LqRequestStatus status) {

    String json = new LqRequestJsonBuilder().toJson(request, status);

    // Jersey does not allow entity value to be null.
    Entity entity = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
    WebTarget webTarget = client.target(baseUrl);

    return webTarget.request(MediaType.APPLICATION_JSON_TYPE)
        .header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
        .put(entity);
  }

  protected void sendAttachment(LqRequest request, LqAttachment attachment, Link attachmentLink) {
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
      callbacks.callFailure(request, attachment, new LqException(msg));
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
