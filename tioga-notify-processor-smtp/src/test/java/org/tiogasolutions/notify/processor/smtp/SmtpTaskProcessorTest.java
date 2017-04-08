package org.tiogasolutions.notify.processor.smtp;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.notify.kernel.message.HtmlMessage;
import org.tiogasolutions.notify.kernel.message.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainStatus;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskResponse;
import org.tiogasolutions.notify.pub.task.TaskResponseAction;
import org.tiogasolutions.notify.pub.task.TaskStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

@Test
public class SmtpTaskProcessorTest {

    private SmtpTaskProcessor processor = new SmtpTaskProcessor();
    private final URI someUri = URI.create("http://localhost/some-task");

    private String smtpHost;
    private String smtpPort;
    private String smtpAuthType;
    private String smtpUsername;
    private String smtpPassword;
    private String smtpFrom;
    private String smtpRecipients;

    private List<AttachmentInfo> attachments = new ArrayList<>();
    private Map<String, String> traitMap = new HashMap<>();
    private ExceptionInfo exceptionInfo = new ExceptionInfo(new IllegalArgumentException("I need to go to the hospital.", new RuntimeException("My leg hurts", new UnsupportedOperationException("Opps, I tripped.", new NullPointerException("Running with scissors")))));

    public SmtpTaskProcessorTest() {
        processor = new SmtpTaskProcessor();

        attachments.add(new AttachmentInfo("screenshot.png", "image/png"));
        attachments.add(new AttachmentInfo("stack-trace.txt", "text/plain"));

        traitMap.put("test", "true");
        traitMap.put("user", System.getProperty("user.name"));
    }

    @BeforeClass
    public void beforeClass() throws Exception {
        try {
            File dir = new File(System.getProperty("user.home"));
            File file = new File(dir, "tioga-secret.properties");

            Properties properties = new Properties();
            properties.load(new FileInputStream(file));

            smtpHost = properties.getProperty("test_smtpHost");
            smtpPort = properties.getProperty("test_smtpPort");
            smtpAuthType = properties.getProperty("test_smtpAuthType");
            smtpUsername = properties.getProperty("test_smtpUsername");
            smtpPassword = properties.getProperty("test_smtpPassword");
            smtpFrom = properties.getProperty("test_smtpFrom");
            smtpRecipients = properties.getProperty("test_smtpRecipients");

            Assert.assertNotNull(smtpHost);
            Assert.assertNotNull(smtpPort);
            Assert.assertNotNull(smtpAuthType);
            Assert.assertNotNull(smtpUsername);
            Assert.assertNotNull(smtpPassword);
            Assert.assertNotNull(smtpFrom);
            Assert.assertNotNull(smtpRecipients);

        } catch (IOException e) {
            throw new SkipException("Skipping tests", e);
        }
    }

    public void testCreateHtmlContent() throws Exception {

        Map<String, String> argMap = new HashMap<>();
        Map<String, String> argValueMap = new LinkedHashMap<>(argMap);

        Destination destination = new Destination("test", "slack", argMap);
        Task task = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        Notification notification = newNotification("test-topic", "Something really bad just happened.", attachments, exceptionInfo, traitMap);

        ThymeleafMessageBuilder messageBuilder = new ThymeleafMessageBuilder();
        String templatePath = messageBuilder.getTemplatePath(argValueMap, "templatePath", SmtpTaskProcessor.DEFAULT_TEMPLATE_PATH);
        HtmlMessage message = messageBuilder.createHtmlMessage(newDomainProfile(), notification, task, templatePath);

        Assert.assertEquals(message.getSubject(), "NOTIFICATION: Something really bad just happened.");

        String createdAt = notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a zzz"));
        Assert.assertEquals(message.getHtml(), String.format(EXPECTED_HTML, createdAt));
        Assert.assertEquals(message.getBody(), String.format(EXPECTED_BODY, createdAt));
    }

    public void testCreateEmailMessage() throws Exception {
        Map<String, String> argValueMap = BeanUtils.toMap(
                "smtpHost:mail.example.com",
                "smtpPort:587",
                "smtpAuthType:tls",
                "smtpUsername:mickey.mail",
                "smtpPassword:my-little-secret",
                "smtpFrom:goofy@disney.com",
                "smtpRecipients:mickey.mouse@disney.com, minnie.mouse@disney.com");

        EmailMessage emailMessage = processor.createEmailMessage(argValueMap);
        assertEquals(emailMessage.getHost(), "mail.example.com");
        assertEquals(emailMessage.getPort(), "587");
        assertEquals(emailMessage.getUsername(), "mickey.mail");
        assertEquals(emailMessage.getPassword(), "my-little-secret");
        assertEquals(emailMessage.getFrom(), "goofy@disney.com");
        assertEquals(emailMessage.getTo(), Arrays.asList("mickey.mouse@disney.com", "minnie.mouse@disney.com"));
    }

    public void sendWithComplexStatus() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("smtpHost",       smtpHost);
        argMap.put("smtpPort",       smtpPort);
        argMap.put("smtpAuthType",   smtpAuthType);

        argMap.put("smtpUsername",   smtpUsername);
        argMap.put("smtpPassword",   smtpPassword);

        argMap.put("smtpFrom",       smtpFrom);
        argMap.put("smtpRecipients", smtpRecipients);

        Destination destination = new Destination("test", "smtp", argMap);
        Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        String message = String.format("%s server:\n" +
                "  *  Runtime Dir     (solutions.runtime.dir)     %s\n" +
                "  *  Config Dir      (solutions.config.dir)      %s\n" +
                "  *  Logback File    (solutions.log.config)      %s\n" +
                "  *  Spring Path     (solutions.spring.config)   %s\n" +
                "  *  Active Profiles (solutions.active.profiles) %s", "started", null, null, null, null, asList("unit-test"));

        Notification notification = newNotification("test-special", message, attachments, exceptionInfo, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, customTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    private DomainProfile newDomainProfile() {
        return new DomainProfile(
                "777", "r-3", "TestDomain", DomainStatus.ACTIVE,
                "some-api-key", "some-api-passowrd",
                "notification-db", "request-db",
                new RouteCatalog(Collections.emptyList(), Collections.emptyList())
        );
    }

    private Notification newNotification(String topic, String summary, List<AttachmentInfo> attachments, ExceptionInfo exceptionInfo, Map<String, String> traitMap) {

        return new Notification(someUri,
                "999",
                TimeUuid.randomUUID().toString(),
                null,
                topic,
                summary,
                "track-9999",
                ZonedDateTime.now(),
                traitMap,
                Collections.singletonList(new Link("example", "http://example.com")),
                exceptionInfo,
                attachments);
    }

    private static final String EXPECTED_HTML = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
            "\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<!--@thymesVar id=\"it\" type=\"org.tiogasolutions.notify.kernel.message.MessageModel\"-->\n" +
            "<head>\n" +
            "    <title>NOTIFICATION: Something really bad just happened.</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1 style=\"white-space: pre\">Something really bad just happened.</h1>\n" +
            "\n" +
            "<h2 style=\"margin-bottom: 0\">Details</h2>\n" +
            "<ul style=\"margin-top: 0\">\n" +
            "    <li>Topic: <span style=\"font-weight: bold;\">test-topic</span></li>\n" +
            "    <li>Created: <span style=\"font-weight: bold;\">%s</span></li>\n" +
            "    <li>Link: <a style=\"font-weight: bold;\" shape=\"rect\" href=\"http://localhost/some-task\">http://localhost/some-task</a></li>\n" +
            "</ul>\n" +
            "\n" +
            "<h2 style=\"margin-bottom: 0\">Traits</h2>\n" +
            "<ul style=\"margin-top: 0\">\n" +
            "    <li>\n" +
            "        <span>test</span>:\n" +
            "        <span style=\"font-weight: bold\">true</span></li>\n" +
            "    <li>\n" +
            "        <span>user</span>:\n" +
            "        <span style=\"font-weight: bold\">jacob</span></li>\n" +
            "</ul>\n" +
            "\n" +
            "<h2 style=\"margin-bottom: 0\">Exception Details</h2>\n" +
            "<ul style=\"margin-top: 0\">\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">I need to go to the hospital.</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.IllegalArgumentException</span>)\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">My leg hurts</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.RuntimeException</span>)\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">Opps, I tripped.</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.UnsupportedOperationException</span>)\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">Running with scissors</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.NullPointerException</span>)\n" +
            "    </li>\n" +
            "</ul>\n" +
            "\n" +
            "<div style=\"margin-top: 2em; font-size: smaller\">\n" +
            "    Bought to you by the people that would rather code than eat, HN &amp; JDP\n" +
            "</div>\n" +
            "\n" +
            "</body>\n" +
            "</html>";

    private static final String EXPECTED_BODY = "\n" +
            "<h1 style=\"white-space: pre\">Something really bad just happened.</h1>\n" +
            "\n" +
            "<h2 style=\"margin-bottom: 0\">Details</h2>\n" +
            "<ul style=\"margin-top: 0\">\n" +
            "    <li>Topic: <span style=\"font-weight: bold;\">test-topic</span></li>\n" +
            "    <li>Created: <span style=\"font-weight: bold;\">%s</span></li>\n" +
            "    <li>Link: <a style=\"font-weight: bold;\" shape=\"rect\" href=\"http://localhost/some-task\">http://localhost/some-task</a></li>\n" +
            "</ul>\n" +
            "\n" +
            "<h2 style=\"margin-bottom: 0\">Traits</h2>\n" +
            "<ul style=\"margin-top: 0\">\n" +
            "    <li>\n" +
            "        <span>test</span>:\n" +
            "        <span style=\"font-weight: bold\">true</span></li>\n" +
            "    <li>\n" +
            "        <span>user</span>:\n" +
            "        <span style=\"font-weight: bold\">jacob</span></li>\n" +
            "</ul>\n" +
            "\n" +
            "<h2 style=\"margin-bottom: 0\">Exception Details</h2>\n" +
            "<ul style=\"margin-top: 0\">\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">I need to go to the hospital.</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.IllegalArgumentException</span>)\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">My leg hurts</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.RuntimeException</span>)\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">Opps, I tripped.</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.UnsupportedOperationException</span>)\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <span style=\"font-weight: bold\">Running with scissors</span>\n" +
            "        (<span style=\"font-style: italic\">java.lang.NullPointerException</span>)\n" +
            "    </li>\n" +
            "</ul>\n" +
            "\n" +
            "<div style=\"margin-top: 2em; font-size: smaller\">\n" +
            "    Bought to you by the people that would rather code than eat, HN &amp; JDP\n" +
            "</div>\n" +
            "\n";

}