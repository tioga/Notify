package org.lqnotify.pub;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;

@Test
public class ExceptionTraceElementTest {

  private JsonTranslator translator;

  @BeforeClass
  public void beforeClass() throws Exception {
    translator = new TiogaJacksonTranslator();
  }

  public void testTranslation() throws Exception {
    TestExceptionA exception = new TestExceptionA("I don't know *WHAT* you are talking about!");
    StackTraceElement element = exception.getStackTrace()[0];
    ExceptionTraceElement oldValue = new ExceptionTraceElement(element);

    String json = translator.toJson(oldValue);
    assertEquals(json, EXPECTED_JSON);

    ExceptionTraceElement newValue = translator.fromJson(ExceptionTraceElement.class, json);
    assertEquals(newValue.getClassName(), oldValue.getClassName());
    assertEquals(newValue.getFileName(), oldValue.getFileName());
    assertEquals(newValue.getMethodName(), oldValue.getMethodName());
    assertEquals(newValue.getLineNumber(), oldValue.getLineNumber());
  }

  private static final String EXPECTED_JSON = "{\n" +
      "  \"className\" : \"org.lqnotify.pub.TestExceptionA\",\n" +
      "  \"methodName\" : \"topMethod\",\n" +
      "  \"fileName\" : \"TestExceptionA.java\",\n" +
      "  \"lineNumber\" : 13\n" +
      "}";
}