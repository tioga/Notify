package org.tiogasolutions.notify.pub;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;

import static org.testng.Assert.assertEquals;

@Test
public class AttachmentInfoTest {

    private static final String EXPECTED_JSON = "{\n" +
            "  \"name\" : \"screen-shot\",\n" +
            "  \"contentType\" : \"image/png\"\n" +
            "}";
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
}