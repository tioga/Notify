package org.tiogasolutions.notify.notifier.request;

import org.tiogasolutions.notify.notifier.NotifierException;
import org.tiogasolutions.notify.notifier.builder.NotificationTrait;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequestJsonBuilder;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.notifier.send.NotificationAttachment;
import org.tiogasolutions.notify.notifier.send.NotificationExceptionInfo;
import org.tiogasolutions.notify.notifier.send.NotificationLink;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequest;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * User: Harlan
 * Date: 1/26/2015
 * Time: 11:14 PM
 */
public class SendNotificationRequestJsonBuilderTest {

  @Test
  public void requestToJson() {

    NotifierException exception = new NotifierException("Ex Message", new Throwable("Root Cause"));
    NotificationExceptionInfo exceptionInfo = new NotificationExceptionInfo(exception);
    List<NotificationTrait> traits = NotificationTrait.toTraits("key1:value1", "key2:value2", "key3:", "key4");

    List<NotificationAttachment> attachments = new ArrayList<>();
    byte[] content = "some text".getBytes(StandardCharsets.UTF_8);
    attachments.add(new NotificationAttachment("name1", "text/plain", content));

    NotificationLink link1 = new NotificationLink("example", "http://example.com");
    NotificationLink link2 = new NotificationLink("google", "http://google.com");

    SendNotificationRequest request = new SendNotificationRequest(
      "topic1",
      "summary1",
      "traceId1",
      ZonedDateTime.now(),
      NotificationTrait.toTraitMap(traits),
      Arrays.asList(link1, link2),
      exceptionInfo,
      attachments);

    String json = new SendNotificationRequestJsonBuilder().toJson(request, SendNotificationRequest.Status.READY);

    System.out.println("Json: " + json);

    assertNotNull(json);
  }
}
