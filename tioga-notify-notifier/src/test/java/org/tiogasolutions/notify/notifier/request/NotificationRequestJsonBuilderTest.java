package org.tiogasolutions.notify.notifier.request;

import org.tiogasolutions.notify.notifier.NotifierException;
import org.tiogasolutions.notify.notifier.builder.NotificationTrait;
import org.tiogasolutions.notify.notifier.json.NotificationRequestJsonBuilder;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * User: Harlan
 * Date: 1/26/2015
 * Time: 11:14 PM
 */
public class NotificationRequestJsonBuilderTest {

  @Test
  public void requestToJson() {

    NotifierException exception = new NotifierException("Ex Message", new Throwable("Root Cause"));
    NotificationExceptionInfo exceptionInfo = new NotificationExceptionInfo(exception);
    List<NotificationTrait> traits = NotificationTrait.toTraits("key1:value1", "key2:value2", "key3:", "key4");

    List<NotificationAttachment> attachments = new ArrayList<>();
    byte[] content = "some text".getBytes(StandardCharsets.UTF_8);
    attachments.add(new NotificationAttachment("name1", "text/plain", content));

    NotificationLink link = new NotificationLink("google", "http://google.com");

    NotificationRequest request = new NotificationRequest(
      "topic1",
      "summary1",
      "traceId1",
      ZonedDateTime.now(),
      NotificationTrait.toTraitMap(traits),
      Collections.singletonList(link),
      exceptionInfo,
      attachments);

    String json = new NotificationRequestJsonBuilder().toJson(request, NotificationRequest.Status.READY);

    assertNotNull(json);
  }
}
