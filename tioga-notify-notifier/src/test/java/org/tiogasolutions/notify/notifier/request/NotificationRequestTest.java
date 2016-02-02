package org.tiogasolutions.notify.notifier.request;

import org.tiogasolutions.notify.notifier.send.NotificationAttachment;
import org.tiogasolutions.notify.notifier.send.NotificationExceptionInfo;
import org.tiogasolutions.notify.notifier.send.NotificationLink;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequest;
import org.tiogasolutions.notify.notifier.uuid.TimeUuid;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequestJsonBuilder;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 10:55 PM
 */
@Test
public class NotificationRequestTest {

  @Test(enabled=false) // very fragile.
  public void jsonTranslationTest() throws Exception {

    String id = TimeUuid.randomUUID().toString();

    Map<String, String> traitMap = new LinkedHashMap<>();
    traitMap.put("sex", "male");
    traitMap.put("color", "blue");
    traitMap.put("length", null);

    SendNotificationRequest request = new SendNotificationRequest(
        "topic",
        "summary",
        "trackingId",
        ZonedDateTime.now(),
        traitMap,
        Collections.singletonList(new NotificationLink("google", "http://google.om")),
        new NotificationExceptionInfo(new RuntimeException("Oops I tripped", new IOException("some thing"))),
        Arrays.asList(
          new NotificationAttachment("bla", "text/plain", "bla, bla, bla"),
          new NotificationAttachment("moo", "text/plain", "moo, moo, moo")));

    String requestJson = new SendNotificationRequestJsonBuilder().toJson(request, SendNotificationRequest.Status.SENDING);

    String expected = String.format(EXPECTED_JSON, request.getCreatedAt());
    assertEquals(requestJson, expected);
  }

  private static final String EXPECTED_JSON = "{%n" +
    "  \"requestStatus\" : \"SENDING\",%n" +
    "  \"topic\" : \"topic\",%n" +
    "  \"summary\" : \"summary\",%n" +
    "  \"trackingId\" : \"trackingId\",%n" +
    "  \"createdAt\" : \"%s\",%n" +
    "  \"traitMap\" : {%n" +
    "    \"sex\" : \"male\",%n" +
    "    \"color\" : \"blue\",%n" +
    "    \"length\" : null%n" +
    "  },%n" +
    "  \"exceptionInfo\" : {%n" +
    "    \"exceptionType\" : \"java.lang.RuntimeException\",%n" +
    "    \"message\" : \"Oops I tripped\",%n" +
    "    \"stackTrace\" : [ {%n" +
    "      \"className\" : \"org.tiogasolutions.notify.notifier.request.NotificationRequestEntityTest\",%n" +
    "      \"methodName\" : \"jsonTranslationTest\",%n" +
    "      \"fileName\" : \"NotificationRequestEntityTest.java\",%n" +
    "      \"lineNumber\" : 36%n" +
    "    }, {%n" +
    "      \"className\" : \"sun.reflect.NativeMethodAccessorImpl\",%n" +
    "      \"methodName\" : \"invoke0\",%n" +
    "      \"fileName\" : \"NativeMethodAccessorImpl.java\",%n" +
    "      \"lineNumber\" : -2%n" +
    "    }, {%n" +
    "      \"className\" : \"sun.reflect.NativeMethodAccessorImpl\",%n" +
    "      \"methodName\" : \"invoke\",%n" +
    "      \"fileName\" : \"NativeMethodAccessorImpl.java\",%n" +
    "      \"lineNumber\" : 62%n" +
    "    }, {%n" +
    "      \"className\" : \"sun.reflect.DelegatingMethodAccessorImpl\",%n" +
    "      \"methodName\" : \"invoke\",%n" +
    "      \"fileName\" : \"DelegatingMethodAccessorImpl.java\",%n" +
    "      \"lineNumber\" : 43%n" +
    "    }, {%n" +
    "      \"className\" : \"java.lang.reflect.Method\",%n" +
    "      \"methodName\" : \"invoke\",%n" +
    "      \"fileName\" : \"Method.java\",%n" +
    "      \"lineNumber\" : 483%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.internal.MethodInvocationHelper\",%n" +
    "      \"methodName\" : \"invokeMethod\",%n" +
    "      \"fileName\" : \"MethodInvocationHelper.java\",%n" +
    "      \"lineNumber\" : 85%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.internal.Invoker\",%n" +
    "      \"methodName\" : \"invokeMethod\",%n" +
    "      \"fileName\" : \"Invoker.java\",%n" +
    "      \"lineNumber\" : 696%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.internal.Invoker\",%n" +
    "      \"methodName\" : \"invokeTestMethod\",%n" +
    "      \"fileName\" : \"Invoker.java\",%n" +
    "      \"lineNumber\" : 882%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.internal.Invoker\",%n" +
    "      \"methodName\" : \"invokeTestMethods\",%n" +
    "      \"fileName\" : \"Invoker.java\",%n" +
    "      \"lineNumber\" : 1189%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.internal.TestMethodWorker\",%n" +
    "      \"methodName\" : \"invokeTestMethods\",%n" +
    "      \"fileName\" : \"TestMethodWorker.java\",%n" +
    "      \"lineNumber\" : 124%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.internal.TestMethodWorker\",%n" +
    "      \"methodName\" : \"run\",%n" +
    "      \"fileName\" : \"TestMethodWorker.java\",%n" +
    "      \"lineNumber\" : 108%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.TestRunner\",%n" +
    "      \"methodName\" : \"privateRun\",%n" +
    "      \"fileName\" : \"TestRunner.java\",%n" +
    "      \"lineNumber\" : 767%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.TestRunner\",%n" +
    "      \"methodName\" : \"run\",%n" +
    "      \"fileName\" : \"TestRunner.java\",%n" +
    "      \"lineNumber\" : 617%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.SuiteRunner\",%n" +
    "      \"methodName\" : \"runTest\",%n" +
    "      \"fileName\" : \"SuiteRunner.java\",%n" +
    "      \"lineNumber\" : 348%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.SuiteRunner\",%n" +
    "      \"methodName\" : \"runSequentially\",%n" +
    "      \"fileName\" : \"SuiteRunner.java\",%n" +
    "      \"lineNumber\" : 343%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.SuiteRunner\",%n" +
    "      \"methodName\" : \"privateRun\",%n" +
    "      \"fileName\" : \"SuiteRunner.java\",%n" +
    "      \"lineNumber\" : 305%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.SuiteRunner\",%n" +
    "      \"methodName\" : \"run\",%n" +
    "      \"fileName\" : \"SuiteRunner.java\",%n" +
    "      \"lineNumber\" : 254%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.SuiteRunnerWorker\",%n" +
    "      \"methodName\" : \"runSuite\",%n" +
    "      \"fileName\" : \"SuiteRunnerWorker.java\",%n" +
    "      \"lineNumber\" : 52%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.SuiteRunnerWorker\",%n" +
    "      \"methodName\" : \"run\",%n" +
    "      \"fileName\" : \"SuiteRunnerWorker.java\",%n" +
    "      \"lineNumber\" : 86%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.TestNG\",%n" +
    "      \"methodName\" : \"runSuitesSequentially\",%n" +
    "      \"fileName\" : \"TestNG.java\",%n" +
    "      \"lineNumber\" : 1224%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.TestNG\",%n" +
    "      \"methodName\" : \"runSuitesLocally\",%n" +
    "      \"fileName\" : \"TestNG.java\",%n" +
    "      \"lineNumber\" : 1149%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.TestNG\",%n" +
    "      \"methodName\" : \"run\",%n" +
    "      \"fileName\" : \"TestNG.java\",%n" +
    "      \"lineNumber\" : 1057%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.remote.RemoteTestNG\",%n" +
    "      \"methodName\" : \"run\",%n" +
    "      \"fileName\" : \"RemoteTestNG.java\",%n" +
    "      \"lineNumber\" : 111%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.remote.RemoteTestNG\",%n" +
    "      \"methodName\" : \"initAndRun\",%n" +
    "      \"fileName\" : \"RemoteTestNG.java\",%n" +
    "      \"lineNumber\" : 204%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.remote.RemoteTestNG\",%n" +
    "      \"methodName\" : \"main\",%n" +
    "      \"fileName\" : \"RemoteTestNG.java\",%n" +
    "      \"lineNumber\" : 175%n" +
    "    }, {%n" +
    "      \"className\" : \"org.testng.RemoteTestNGStarter\",%n" +
    "      \"methodName\" : \"main\",%n" +
    "      \"fileName\" : \"RemoteTestNGStarter.java\",%n" +
    "      \"lineNumber\" : 125%n" +
    "    } ],%n" +
    "    \"cause\" : {%n" +
    "      \"exceptionType\" : \"java.io.IOException\",%n" +
    "      \"message\" : \"some thing\",%n" +
    "      \"stackTrace\" : [ {%n" +
    "        \"className\" : \"org.tiogasolutions.notify.notifier.request.NotificationRequestEntityTest\",%n" +
    "        \"methodName\" : \"jsonTranslationTest\",%n" +
    "        \"fileName\" : \"NotificationRequestEntityTest.java\",%n" +
    "        \"lineNumber\" : 36%n" +
    "      }, {%n" +
    "        \"className\" : \"sun.reflect.NativeMethodAccessorImpl\",%n" +
    "        \"methodName\" : \"invoke0\",%n" +
    "        \"fileName\" : \"NativeMethodAccessorImpl.java\",%n" +
    "        \"lineNumber\" : -2%n" +
    "      }, {%n" +
    "        \"className\" : \"sun.reflect.NativeMethodAccessorImpl\",%n" +
    "        \"methodName\" : \"invoke\",%n" +
    "        \"fileName\" : \"NativeMethodAccessorImpl.java\",%n" +
    "        \"lineNumber\" : 62%n" +
    "      }, {%n" +
    "        \"className\" : \"sun.reflect.DelegatingMethodAccessorImpl\",%n" +
    "        \"methodName\" : \"invoke\",%n" +
    "        \"fileName\" : \"DelegatingMethodAccessorImpl.java\",%n" +
    "        \"lineNumber\" : 43%n" +
    "      }, {%n" +
    "        \"className\" : \"java.lang.reflect.Method\",%n" +
    "        \"methodName\" : \"invoke\",%n" +
    "        \"fileName\" : \"Method.java\",%n" +
    "        \"lineNumber\" : 483%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.internal.MethodInvocationHelper\",%n" +
    "        \"methodName\" : \"invokeMethod\",%n" +
    "        \"fileName\" : \"MethodInvocationHelper.java\",%n" +
    "        \"lineNumber\" : 85%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.internal.Invoker\",%n" +
    "        \"methodName\" : \"invokeMethod\",%n" +
    "        \"fileName\" : \"Invoker.java\",%n" +
    "        \"lineNumber\" : 696%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.internal.Invoker\",%n" +
    "        \"methodName\" : \"invokeTestMethod\",%n" +
    "        \"fileName\" : \"Invoker.java\",%n" +
    "        \"lineNumber\" : 882%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.internal.Invoker\",%n" +
    "        \"methodName\" : \"invokeTestMethods\",%n" +
    "        \"fileName\" : \"Invoker.java\",%n" +
    "        \"lineNumber\" : 1189%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.internal.TestMethodWorker\",%n" +
    "        \"methodName\" : \"invokeTestMethods\",%n" +
    "        \"fileName\" : \"TestMethodWorker.java\",%n" +
    "        \"lineNumber\" : 124%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.internal.TestMethodWorker\",%n" +
    "        \"methodName\" : \"run\",%n" +
    "        \"fileName\" : \"TestMethodWorker.java\",%n" +
    "        \"lineNumber\" : 108%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.TestRunner\",%n" +
    "        \"methodName\" : \"privateRun\",%n" +
    "        \"fileName\" : \"TestRunner.java\",%n" +
    "        \"lineNumber\" : 767%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.TestRunner\",%n" +
    "        \"methodName\" : \"run\",%n" +
    "        \"fileName\" : \"TestRunner.java\",%n" +
    "        \"lineNumber\" : 617%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.SuiteRunner\",%n" +
    "        \"methodName\" : \"runTest\",%n" +
    "        \"fileName\" : \"SuiteRunner.java\",%n" +
    "        \"lineNumber\" : 348%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.SuiteRunner\",%n" +
    "        \"methodName\" : \"runSequentially\",%n" +
    "        \"fileName\" : \"SuiteRunner.java\",%n" +
    "        \"lineNumber\" : 343%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.SuiteRunner\",%n" +
    "        \"methodName\" : \"privateRun\",%n" +
    "        \"fileName\" : \"SuiteRunner.java\",%n" +
    "        \"lineNumber\" : 305%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.SuiteRunner\",%n" +
    "        \"methodName\" : \"run\",%n" +
    "        \"fileName\" : \"SuiteRunner.java\",%n" +
    "        \"lineNumber\" : 254%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.SuiteRunnerWorker\",%n" +
    "        \"methodName\" : \"runSuite\",%n" +
    "        \"fileName\" : \"SuiteRunnerWorker.java\",%n" +
    "        \"lineNumber\" : 52%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.SuiteRunnerWorker\",%n" +
    "        \"methodName\" : \"run\",%n" +
    "        \"fileName\" : \"SuiteRunnerWorker.java\",%n" +
    "        \"lineNumber\" : 86%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.TestNG\",%n" +
    "        \"methodName\" : \"runSuitesSequentially\",%n" +
    "        \"fileName\" : \"TestNG.java\",%n" +
    "        \"lineNumber\" : 1224%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.TestNG\",%n" +
    "        \"methodName\" : \"runSuitesLocally\",%n" +
    "        \"fileName\" : \"TestNG.java\",%n" +
    "        \"lineNumber\" : 1149%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.TestNG\",%n" +
    "        \"methodName\" : \"run\",%n" +
    "        \"fileName\" : \"TestNG.java\",%n" +
    "        \"lineNumber\" : 1057%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.remote.RemoteTestNG\",%n" +
    "        \"methodName\" : \"run\",%n" +
    "        \"fileName\" : \"RemoteTestNG.java\",%n" +
    "        \"lineNumber\" : 111%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.remote.RemoteTestNG\",%n" +
    "        \"methodName\" : \"initAndRun\",%n" +
    "        \"fileName\" : \"RemoteTestNG.java\",%n" +
    "        \"lineNumber\" : 204%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.remote.RemoteTestNG\",%n" +
    "        \"methodName\" : \"main\",%n" +
    "        \"fileName\" : \"RemoteTestNG.java\",%n" +
    "        \"lineNumber\" : 175%n" +
    "      }, {%n" +
    "        \"className\" : \"org.testng.RemoteTestNGStarter\",%n" +
    "        \"methodName\" : \"main\",%n" +
    "        \"fileName\" : \"RemoteTestNGStarter.java\",%n" +
    "        \"lineNumber\" : 125%n" +
    "      } ],%n" +
    "      \"cause\" : null%n" +
    "    }%n" +
    "  }%n" +
    "}";
}
