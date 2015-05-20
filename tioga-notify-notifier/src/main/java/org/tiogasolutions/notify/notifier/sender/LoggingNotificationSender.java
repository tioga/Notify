package org.tiogasolutions.notify.notifier.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.notify.notifier.json.NotificationRequestJsonBuilder;
import org.tiogasolutions.notify.notifier.request.NotificationRequest;
import org.tiogasolutions.notify.notifier.request.NotificationRequestStatus;
import org.tiogasolutions.notify.notifier.request.NotificationResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:52 AM
 */
public class LoggingNotificationSender extends AbstractNotificationSender {
  private static final Logger log = LoggerFactory.getLogger(LoggingNotificationSender.class);

  private NotificationRequest lastRequest;

  @Override
  public Future<NotificationResponse> send(NotificationRequest request) {

    this.lastRequest = request;
    if (log.isTraceEnabled()) {
      log.trace(new NotificationRequestJsonBuilder().toJson(request, NotificationRequestStatus.READY));
    } else {
      log.debug("Notification {}:{}", lastRequest.getTopic(), lastRequest.getSummary());
    }
    NotificationResponse response = NotificationResponse.newSuccess(request);
    callbacks.callSuccess(response);
    return CompletableFuture.completedFuture(response);
  }

  public NotificationRequest getLastRequest() {
    return lastRequest;
  }

  public void clearLast() {
    lastRequest = null;
  }
}
