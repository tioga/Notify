package org.tiogasolutions.notify.sender.http;

import org.tiogasolutions.notify.notifier.send.SendNotificationRequest;
import org.tiogasolutions.notify.notifier.NotifierException;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequestJsonBuilder;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 1:10 AM
 */
public class SimpleHttpNotificationSender extends HttpNotificationSender {
  private static final Logger log = LoggerFactory.getLogger(SimpleHttpNotificationSender.class);

  public SimpleHttpNotificationSender(HttpNotificationSenderConfig config) {
    super(config);
    log.info("Configured SimpleHttpNotificationSender for " + getBaseUrl());
  }

  @Override
  public Future<SendNotificationResponse> send(SendNotificationRequest request) {
    // Never throw an exception from here.

    Callable<SendNotificationResponse> callable = () -> {
      Response sendResponse;
      try {
        // Send the request
        sendResponse = sendRequest(request, SendNotificationRequest.Status.READY);

      } catch (Exception t) {
        SendNotificationResponse notificationResponse = SendNotificationResponse.newFailure(request, t);
        callbacks.callFailure(notificationResponse);
        log.error("Failure sending notification request: ", t);
        return notificationResponse;
      }

      int status = sendResponse.getStatus();
      if (status == 200 || status == 201) {

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

    return getExecutorService().submit(callable);

  }

  @Override
  protected Response sendRequest(SendNotificationRequest request, SendNotificationRequest.Status status) {

    String json = new SendNotificationRequestJsonBuilder().toJson(request, status);

    // Jersey does not allow entity value to be null.
    Entity entity = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
    WebTarget webTarget = getClient().target(getBaseUrl());

    return webTarget.request(MediaType.APPLICATION_JSON_TYPE)
        .header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
        .post(entity);
  }

}
