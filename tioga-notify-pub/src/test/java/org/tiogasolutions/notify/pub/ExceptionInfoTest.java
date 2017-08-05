package org.tiogasolutions.notify.pub;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;

import java.util.List;

import static org.testng.Assert.*;

@Test
public class ExceptionInfoTest {

    private static final String EXPECTED_JSON = "{\n" +
            "  \"exceptionType\" : \"org.tiogasolutions.notify.pub.TestExceptionB\",\n" +
            "  \"message\" : \"org.tiogasolutions.notify.pub.TestExceptionA: You did something really bad\",\n" +
            "  \"stackTrace\" : \"org.tiogasolutions.notify.pub.TestExceptionB: org.tiogasolutions.notify.pub.TestExceptionA: You did something really bad\\n\\tat org.tiogasolutions.notify.pub.TestExceptionB.firstMethod(TestExceptionB.java:133)\\n\\tat org.tiogasolutions.notify.pub.TestExceptionB.secondMethod(TestExceptionB.java:344)\\n\\tat org.tiogasolutions.notify.pub.TestExceptionB.thirdMethod(TestExceptionB.java:352)\\nCaused by: org.tiogasolutions.notify.pub.TestExceptionA: You did something really bad\\n\\tat org.tiogasolutions.notify.pub.TestExceptionA.topMethod(TestExceptionA.java:13)\\n\\tat org.tiogasolutions.notify.pub.TestExceptionA.middleMethod(TestExceptionA.java:34)\\n\\tat org.tiogasolutions.notify.pub.TestExceptionA.bottomMethod(TestExceptionA.java:32)\\n\",\n" +
            "  \"cause\" : {\n" +
            "    \"exceptionType\" : \"org.tiogasolutions.notify.pub.TestExceptionA\",\n" +
            "    \"message\" : \"You did something really bad\",\n" +
            "    \"stackTrace\" : \"org.tiogasolutions.notify.pub.TestExceptionA: You did something really bad\\n\\tat org.tiogasolutions.notify.pub.TestExceptionA.topMethod(TestExceptionA.java:13)\\n\\tat org.tiogasolutions.notify.pub.TestExceptionA.middleMethod(TestExceptionA.java:34)\\n\\tat org.tiogasolutions.notify.pub.TestExceptionA.bottomMethod(TestExceptionA.java:32)\\n\",\n" +
            "    \"cause\" : null\n" +
            "  }\n" +
            "}";
    private JsonTranslator translator;

    @BeforeClass
    public void beforeClass() throws Exception {
        translator = new TiogaJacksonTranslator();
    }

    public void testChangedExceptions() throws Exception {
        Exception npe = new NullPointerException("Running with scissors.");
        Exception uoe = new UnsupportedOperationException("Opps, I tripped.", npe);
        Exception re = new RuntimeException("My leg hurts.", uoe);
        Exception iae = new IllegalArgumentException("I need to go to the hospital.", re);

        ExceptionInfo info = new ExceptionInfo(iae);
        List<ExceptionInfo> causes = info.getCauses();

        Assert.assertEquals(info.getMessage(), "I need to go to the hospital.");
        Assert.assertEquals(info.getExceptionType(), IllegalArgumentException.class.getName());

        info = info.getCause();
        Assert.assertEquals(info, causes.get(0));
        Assert.assertEquals(info.getMessage(), "My leg hurts.");
        Assert.assertEquals(info.getExceptionType(), RuntimeException.class.getName());

        info = info.getCause();
        Assert.assertEquals(info, causes.get(1));
        Assert.assertEquals(info.getMessage(), "Opps, I tripped.");
        Assert.assertEquals(info.getExceptionType(), UnsupportedOperationException.class.getName());

        info = info.getCause();
        Assert.assertEquals(info, causes.get(2));
        Assert.assertEquals(info.getMessage(), "Running with scissors.");
        Assert.assertEquals(info.getExceptionType(), NullPointerException.class.getName());

    }

    public void testTranslateExceptionInfo() throws Exception {
        try {
            throw new TestExceptionA("You did something really bad");

        } catch (TestExceptionA exceptionA) {
            TestExceptionB exceptionB = new TestExceptionB(exceptionA);
            ExceptionInfo oldValue = new ExceptionInfo(exceptionB);
            ExceptionInfo oldCause = oldValue.getCause();

            String json = translator.toJson(oldValue);
            assertEquals(json, EXPECTED_JSON);

            ExceptionInfo newValue = translator.fromJson(ExceptionInfo.class, json);
            ExceptionInfo newCause = newValue.getCause();

            assertEquals(newValue.getMessage(), oldValue.getMessage());

            assertEquals(newValue.getStackTrace(), oldValue.getStackTrace());
            assertNotNull(newCause);

            assertEquals(newCause.getMessage(), oldCause.getMessage());
            assertEquals(newCause.getStackTrace(), oldCause.getStackTrace());
            assertNull(newCause.getCause());
        }
    }
}