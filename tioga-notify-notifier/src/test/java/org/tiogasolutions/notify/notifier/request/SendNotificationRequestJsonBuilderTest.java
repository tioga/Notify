package org.tiogasolutions.notify.notifier.request;

import org.testng.annotations.Test;
import org.tiogasolutions.notify.notifier.NotifierException;
import org.tiogasolutions.notify.notifier.builder.NotificationTrait;
import org.tiogasolutions.notify.notifier.send.*;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Test
public class SendNotificationRequestJsonBuilderTest {

    public void requestToJson() {

        NotifierException exception = new NotifierException("Ex Message", new Throwable("Root Cause"));
        NotificationExceptionInfo exceptionInfo = new NotificationExceptionInfo(exception);
        List<NotificationTrait> traits = NotificationTrait.toTraits("key1:value1", "key2:value2", "key3:", "key4");

        List<NotificationAttachment> attachments = new ArrayList<>();
        byte[] content = "some text".getBytes(StandardCharsets.UTF_8);
        attachments.add(new NotificationAttachment("name1", "text/plain", content));

        NotificationLink link1 = new NotificationLink("example", "http://example.com");
        NotificationLink link2 = new NotificationLink("google", "http://google.com");

        SendNotificationRequest request = new SendNotificationRequest(
                true,
                "topic1",
                "summary1",
                "traceId1",
                ZonedDateTime.now(),
                NotificationTrait.toTraitMap(traits),
                Arrays.asList(link1, link2),
                exceptionInfo,
                attachments);

        String actual = new SendNotificationRequestJsonBuilder()
                .toJson(request, SendNotificationRequest.Status.READY);

        System.out.println(actual);
    }

//    public void complexException() {
//        ScriptException ex = new ScriptException("ReferenceError: \"notification\" is not defined in <eval> at line number 2", "<eval>", 1, -1);
//
//        SendNotificationRequest request = new SendNotificationRequest(
//                true,
//                "testing-123",
//                "Something bad just happened.",
//                null,
//                ZonedDateTime.now(),
//                null,
//                null,
//                new NotificationExceptionInfo(ex),
//                null);
//
//        String actual = new SendNotificationRequestJsonBuilder()
//                .toJson(request, SendNotificationRequest.Status.READY);
//
//        System.out.println();
//        System.out.println(actual);
//        System.out.println();
//    }

    public void complexRequestToJson() {

        String msg = "Starting server:\n" +
                "  *  Runtime Dir     (solutions.runtime.dir)     null\n" +
                "  *  Config Dir      (solutions.config.dir)      null\n" +
                "  *  Logback File    (solutions.log.config)      null\n" +
                "  *  Spring Path     (solutions.spring.config)   classpath:/tioga-solutions-engine/spring-config.xml\n" +
                "  *  Active Profiles (solutions.active.profiles) [hosted]";

        SendNotificationRequest request = new SendNotificationRequest(
                true,
                "topic1",
                msg,
                "traceId1",
                ZonedDateTime.now(),
                null,
                null,
                null,
                null);

        String actual = new SendNotificationRequestJsonBuilder()
                .toJson(request, SendNotificationRequest.Status.READY);

        System.out.println(actual);
    }
}
