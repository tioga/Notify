package org.tiogasolutions.notifyserver.pub;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.testng.annotations.*;
import org.tiogasolutions.notifyserver.pub.AttachmentInfo;

import static org.testng.Assert.assertEquals;

@Test
public class AttachmentInfoTest {

  private JsonTranslator translator;

  @BeforeClass
  public void beforeClass() throws Exception {
    translator = new TiogaJacksonTranslator();
  }

  public void testTranslation() throws Exception {
    AttachmentInfo oldValue = new AttachmentInfo("screen-shot", "image/png");

    String json = translator.toJson(oldValue);
    assertEquals(json, EXPECTED_JSON);

    AttachmentInfo newValue = translator.fromJson(AttachmentInfo.class, json);
    assertEquals(newValue.getName(), oldValue.getName());
    assertEquals(newValue.getContentType(), oldValue.getContentType());
  }

  private static final String EXPECTED_JSON = "{\n" +
    "  \"name\" : \"screen-shot\",\n" +
    "  \"contentType\" : \"image/png\"\n" +
    "}";
}