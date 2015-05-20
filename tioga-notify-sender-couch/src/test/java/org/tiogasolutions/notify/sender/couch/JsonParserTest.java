package org.tiogasolutions.notify.sender.couch;

import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class JsonParserTest {

  public void testSplit() throws Exception {
    JsonParser parser = new JsonParser();
    Map<String,String> extracted = parser.split(TEST_SOURCE, 0);
    assertNotNull(extracted);
    assertEquals(extracted.size(), 5);

    assertEquals(extracted.get("_id"), "bed8a060-cc2d-11e4-a61d-b8ca3a8e2d05");
    assertEquals(extracted.get("_rev"), "6-2e8c0fac4105c93671f4d652b4ce9474");
    assertEquals(extracted.get("entityType"), "NotificationRequest");
    assertEquals(extracted.get("entity"), ENTITY_EXTRACTED);
    assertEquals(extracted.get("_attachments"), ATTACHMENTS_EXTRACTED);
  }

  private static final String ENTITY_EXTRACTED =
    "{\n" +
    "       \"requestStatus\": \"COMPLETED\",\n" +
    "       \"topic\": \"test-main\",\n" +
    "       \"summary\": \"Notification from test main: 0\",\n" +
    "       \"trackingId\": \"bed25ed0-cc2d-11e4-a7c0-b8ca3a8e2d05\",\n" +
    "       \"createdAt\": \"2015-03-16T22:42:37.513Z[GMT]\",\n" +
    "       \"traitMap\": {\n" +
    "           \"key1\": \"value1\",\n" +
    "           \"index\": \"0\"\n" +
    "       },\n" +
    "       \"exceptionInfo\": {\n" +
    "           \"exceptionType\": \"java.lang.Throwable\",\n" +
    "           \"message\": \"This is notification exception\",\n" +
    "           \"stackTrace\": [\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"generateRequests\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 217\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"generateRequestsByCouch\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 199\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp$$Lambda$18/365625031\",\n" +
    "                   \"methodName\": \"execute\",\n" +
    "                   \"fileName\": null,\n" +
    "                   \"lineNumber\": -1\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"run\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 90\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"main\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 42\n" +
    "               }\n" +
    "           ],\n" +
    "           \"cause\": null\n" +
    "       }\n" +
    "   }";

  private static final String ATTACHMENTS_EXTRACTED =
      "{\n" +
      "       \"attachOne\": {\n" +
      "           \"content_type\": \"text/plain\",\n" +
      "           \"revpos\": 2,\n" +
      "           \"digest\": \"md5-54ZD9s9DpwWM2A4EV1oClw==\",\n" +
      "           \"length\": 24,\n" +
      "           \"stub\": true\n" +
      "       },\n" +
      "       \"attachTwo\": {\n" +
      "           \"content_type\": \"text/plain\",\n" +
      "           \"revpos\": 3,\n" +
      "           \"digest\": \"md5-j5Wg+nxGLR+Nux93RaF5qQ==\",\n" +
      "           \"length\": 24,\n" +
      "           \"stub\": true\n" +
      "       }\n" +
      "   }";

  private static final String TEST_SOURCE = "{\n" +
    "   \"_id\": \"bed8a060-cc2d-11e4-a61d-b8ca3a8e2d05\",\n" +
    "   \"_rev\": \"6-2e8c0fac4105c93671f4d652b4ce9474\",\n" +
    "   \"entityType\": \"NotificationRequest\",\n" +
    "   \"entity\": {\n" +
    "       \"requestStatus\": \"COMPLETED\",\n" +
    "       \"topic\": \"test-main\",\n" +
    "       \"summary\": \"Notification from test main: 0\",\n" +
    "       \"trackingId\": \"bed25ed0-cc2d-11e4-a7c0-b8ca3a8e2d05\",\n" +
    "       \"createdAt\": \"2015-03-16T22:42:37.513Z[GMT]\",\n" +
    "       \"traitMap\": {\n" +
    "           \"key1\": \"value1\",\n" +
    "           \"index\": \"0\"\n" +
    "       },\n" +
    "       \"exceptionInfo\": {\n" +
    "           \"exceptionType\": \"java.lang.Throwable\",\n" +
    "           \"message\": \"This is notification exception\",\n" +
    "           \"stackTrace\": [\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"generateRequests\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 217\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"generateRequestsByCouch\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 199\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp$$Lambda$18/365625031\",\n" +
    "                   \"methodName\": \"execute\",\n" +
    "                   \"fileName\": null,\n" +
    "                   \"lineNumber\": -1\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"run\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 90\n" +
    "               },\n" +
    "               {\n" +
    "                   \"className\": \"org.tiogasolutions.notify.client.AdminApp\",\n" +
    "                   \"methodName\": \"main\",\n" +
    "                   \"fileName\": \"AdminApp.java\",\n" +
    "                   \"lineNumber\": 42\n" +
    "               }\n" +
    "           ],\n" +
    "           \"cause\": null\n" +
    "       }\n" +
    "   },\n" +
    "   \"_attachments\": {\n" +
    "       \"attachOne\": {\n" +
    "           \"content_type\": \"text/plain\",\n" +
    "           \"revpos\": 2,\n" +
    "           \"digest\": \"md5-54ZD9s9DpwWM2A4EV1oClw==\",\n" +
    "           \"length\": 24,\n" +
    "           \"stub\": true\n" +
    "       },\n" +
    "       \"attachTwo\": {\n" +
    "           \"content_type\": \"text/plain\",\n" +
    "           \"revpos\": 3,\n" +
    "           \"digest\": \"md5-j5Wg+nxGLR+Nux93RaF5qQ==\",\n" +
    "           \"length\": 24,\n" +
    "           \"stub\": true\n" +
    "       }\n" +
    "   }\n" +
    "}";
}