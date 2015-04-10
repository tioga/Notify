package org.tiogasolutions.notify.notifier.request;

import org.tiogasolutions.notify.notifier.LqException;
import org.tiogasolutions.notify.notifier.builder.LqTrait;
import org.tiogasolutions.notify.notifier.json.LqRequestJsonBuilder;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;

/**
 * User: Harlan
 * Date: 1/26/2015
 * Time: 11:14 PM
 */
public class LqRequestJsonBuilderTest {

  @Test
  public void requestToJson() {

    LqException exception = new LqException("Ex Message", new Throwable("Root Cause"));
    LqExceptionInfo exceptionInfo = new LqExceptionInfo(exception);
    List<LqTrait> traits = LqTrait.toTraits("key1:value1", "key2:value2", "key3:", "key4");

    List<LqAttachment> attachments = new ArrayList<>();
    byte[] content = "some text".getBytes(StandardCharsets.UTF_8);
    attachments.add(new LqAttachment("name1", "text/plain", content));

    LqRequest request = new LqRequest(
      "topic1",
      "summary1",
      "traceId1",
      ZonedDateTime.now(),
      LqTrait.toTraitMap(traits),
      exceptionInfo,
      attachments);

    String json = new LqRequestJsonBuilder().toJson(request, LqRequestStatus.READY);

    assertNotNull(json);
  }
}
