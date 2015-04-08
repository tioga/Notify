package org.lqnotify.sender.couch;

import org.lqnotify.notifier.LqNotifier;
import org.lqnotify.notifier.request.LqResponse;
import org.lqnotify.notifier.request.LqResponseType;
import org.lqnotify.notifier.sender.LqSimpleSender;
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

    LqHttpSenderConfig config = new LqHttpSenderConfig()
        .setUrl("http://localhost:8080/lq-server/api/v1/client/simple-request-entry")
        .setUserName("KGQZZ-2940190")
        .setPassword("GoFish");

    LqSimpleHttpSender httpSender = new LqSimpleHttpSender(config);
    LqNotifier notifier = new LqNotifier(httpSender);

    log.info("Building notification");
    byte[] attachBytes = "this is some attachment text".getBytes();
    Future<LqResponse> responseFuture = notifier.begin()
        .topic("test topic")
        .trackingId("trace this")
        .summary("Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("some", MediaType.TEXT_PLAIN, attachBytes)
        .send();

    log.info("Sending notification");
    try {
      LqResponse response = responseFuture.get();
      if (response.getResponseType() == LqResponseType.SUCCESS) {
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
