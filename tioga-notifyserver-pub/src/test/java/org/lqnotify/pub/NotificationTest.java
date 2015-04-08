package org.lqnotify.pub;

import java.net.URI;
import java.util.Collections;
import org.tiogasolutions.dev.common.DateUtils;
import org.tiogasolutions.dev.common.fine.TraitMap;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;

@Test
public class NotificationTest {

  private JsonTranslator translator;

  @BeforeClass
  public void beforeClass() throws Exception {
    translator = new TiogaJacksonTranslator();
  }

/*
  public void testTranslation() throws Exception {
    Notification oldValue = new Notification(
      URI.create("http://www.whatever.com"),
      "asdf-123",
      "revision-1.2",
      "track-4321",
      "unit-test",
      "This is just a test notifier.",
      NotificationStatus.FAILED,
      DateUtils.toZonedDateTime("2015-01-25T20:38:38.402-08:00[America/Los_Angeles]"),
      new TraitMap("one:1", "two:2", "color:blue", "boy:girl"),
      Collections.emptyList(),
      true,
      Collections.emptyList()
    );

    String json = translator.toJson(oldValue);
    assertEquals(json, EXPECTED_JSON);

    Notification newValue = translator.fromJson(Notification.class, json);
    assertEquals(newValue.getId(), oldValue.getId());
    // TODO - need to add the rest of the asserts.
  }
*/

  private static final String EXPECTED_JSON = "{\n" +
    "  \"self\" : \"http://www.whatever.com\",\n" +
    "  \"id\" : \"asdf-123\",\n" +
    "  \"revision\" : \"revision-1.2\",\n" +
    "  \"trackingId\" : \"track-4321\",\n" +
    "  \"topic\" : \"unit-test\",\n" +
    "  \"summary\" : \"This is just a test notifier.\",\n" +
    "  \"status\" : \"FAILED\",\n" +
    "  \"createdAt\" : \"2015-01-25T20:38:38.402-08:00[America/Los_Angeles]\",\n" +
    "  \"traitMap\" : {\n" +
    "    \"boy\" : \"girl\",\n" +
    "    \"color\" : \"blue\",\n" +
    "    \"one\" : \"1\",\n" +
    "    \"two\" : \"2\"\n" +
    "  },\n" +
    "  \"tasks\" : [ ],\n" +
    "  \"withException\" : true,\n" +
    "  \"attachments\" : [ ]\n" +
    "}";
}