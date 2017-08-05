package org.tiogasolutions.notify.kernel.message;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainStatus;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.DestinationStatus;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskStatus;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Test
public class MessageBuilderTest {

    private Task task;
    private DomainProfile domainProfile;
    private Notification notification;

    private ThymeleafMessageBuilder messageBuilder = new ThymeleafMessageBuilder();

    @BeforeClass
    public void beforeClass() throws Exception {
        Map<String, String> argHashMap = BeanUtils.toMap(
                "smtpHost:mail.example.com",
                "smtpPort:587",
                "smtpAuthType:tls",
                "smtpUsername:mickey.mail",
                "smtpPassword:my-little-secret",
                "smtpFrom:goofy@disney.com",
                "smtpRecipients:mickey.mouse@disney.com, minnie.mouse@disney.com"
        );

        domainProfile = new DomainProfile(
                "777", "r-3", "TestDomain", DomainStatus.ACTIVE,
                "some-api-key", "some-api-passowrd",
                "notification-db", "request-db",
                new RouteCatalog(Collections.emptyList(), Collections.emptyList())
        );

        notification = new Notification(
                URI.create("http://whatever.com/api/v2/notifications/123"),
                "some-domain", "123", "r-1", "test-topic",
                "Something really bad just happened.", "tracking id #321",
                ZonedDateTime.now(),
                BeanUtils.toMap("color:red", "size:medium"),
                Collections.singletonList(new Link("example", "http://example.com")),
                new ExceptionInfo(new RuntimeException("Oops, I tripped.")),
                Arrays.asList(new AttachmentInfo("screenshot.png", "image/png")));

        task = new Task(
                URI.create("http://whatever.com/api/v2/tasks/456"),
                "456",
                "r-9",
                TaskStatus.SENDING,
                "123",
                ZonedDateTime.now(),
                new Destination("E-Mails", "smtp", DestinationStatus.ENABLED, argHashMap),
                null);
    }
}