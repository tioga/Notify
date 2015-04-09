package org.tiogasolutions.notifyserver.notifier.request;

import org.tiogasolutions.notifyserver.notifier.json.LqRequestJsonBuilder;
import org.tiogasolutions.notifyserver.notifier.request.LqAttachment;
import org.tiogasolutions.notifyserver.notifier.request.LqExceptionInfo;
import org.tiogasolutions.notifyserver.notifier.request.LqRequest;
import org.tiogasolutions.notifyserver.notifier.request.LqRequestStatus;
import org.tiogasolutions.notifyserver.notifier.uuid.TimeUuid;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 10:55 PM
 */
@Test
public class LqRequestTest {

  @Test(enabled=false) // very fragile.
  public void jsonTranslationTest() throws Exception {

    String id = TimeUuid.randomUUID().toString();

    Map<String, String> traitMap = new LinkedHashMap<>();
    traitMap.put("sex", "male");
    traitMap.put("color", "blue");
    traitMap.put("length", null);

    LqRequest request = new LqRequest(
        "topic",
        "summary",
        "trackingId",
        ZonedDateTime.now(),
        traitMap,
        new LqExceptionInfo(new RuntimeException("Oops I tripped", new IOException("some thing"))),
        Arrays.asList(
          new LqAttachment("bla", "text/plain", "bla, bla, bla"),
          new LqAttachment("moo", "text/plain", "moo, moo, moo")));

    String lqJson = new LqRequestJsonBuilder().toJson(request, LqRequestStatus.SENDING);

    String expected = String.format(EXPECTED_JSON, request.getCreatedAt());
    assertEquals(lqJson, expected);
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
    "      \"className\" : \"org.tiogasolutions.notifyserver.notifier.request.LqRequestEntityTest\",%n" +
    "      \"methodName\" : \"jsonTranslationTest\",%n" +
    "      \"fileName\" : \"LqRequestEntityTest.java\",%n" +
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
    "        \"className\" : \"org.tiogasolutions.notifyserver.notifier.request.LqRequestEntityTest\",%n" +
    "        \"methodName\" : \"jsonTranslationTest\",%n" +
    "        \"fileName\" : \"LqRequestEntityTest.java\",%n" +
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
