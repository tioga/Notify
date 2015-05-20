package org.tiogasolutions.notify.sender.http;

import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.request.NotificationResponseType;
import org.tiogasolutions.notify.notifier.request.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 10:30 PM
 */
public class HttpSenderTestMain {
  private final static Logger log = LoggerFactory.getLogger(HttpSenderTestMain.class);

  public static void main(String[] args) {

    HttpNotificationSenderConfig config = new HttpNotificationSenderConfig()
        .setUrl("http://localhost:8080/notify-server/api/v1/client/simple-request-entry")
        .setUserName("KGQZZ-2940190")
        .setPassword("GoFish");

    SimpleHttpNotificationSender httpSender = new SimpleHttpNotificationSender(config);
    Notifier notifier = new Notifier(httpSender);

    log.info("Building notification");
    byte[] attachBytes = "this is some attachment text".getBytes();
    Future<NotificationResponse> responseFuture = notifier.begin()
        .topic("test topic")
        .trackingId("trace this")
        .summary("Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("some", MediaType.TEXT_PLAIN, attachBytes)
        .send();

    log.info("Sending notification");
    try {
      NotificationResponse response = responseFuture.get();
      if (response.getResponseType() == NotificationResponseType.SUCCESS) {
        log.info("Notification successful");
      } else {
        log.error("Notification failure.", response.getThrowable());
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    httpSender.dispose();

  }
}
