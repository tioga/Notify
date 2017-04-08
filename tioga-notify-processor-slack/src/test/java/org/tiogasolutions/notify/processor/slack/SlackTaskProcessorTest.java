package org.tiogasolutions.notify.processor.slack;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

@Test
public class SlackTaskProcessorTest {

    private final SlackTaskProcessor processor;
    private final URI someUri = URI.create("http://localhost/some-task");
    private final JsonTranslator translator = new TiogaJacksonTranslator();

    private String tiogaSlackTestUrl = null;

    private List<AttachmentInfo> attachments = new ArrayList<>();
    private Map<String, String> traitMap = new HashMap<>();
    private ExceptionInfo exceptionInfo = new ExceptionInfo(new IllegalArgumentException("I need to go to the hospital.", new RuntimeException("My leg hurts", new UnsupportedOperationException("Opps, I tripped.", new NullPointerException("Running with scissors")))));

    public SlackTaskProcessorTest() {
        processor = new SlackTaskProcessor(translator);

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

            tiogaSlackTestUrl = properties.getProperty("test_tiogaSlackTestUrl");
            Assert.assertNotNull(tiogaSlackTestUrl);

        } catch (IOException e) {
            throw new SkipException("Skipping tests", e);
        }
    }

    public void sendDefaultMessage() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("username", "Test-sendDefaultMessage");
        Destination destination = new Destination("test", "slack", argMap);

        Task task = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);
        Notification notification = newNotification("test-default", "Default test notification", attachments, exceptionInfo, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, task);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    public void sendWithDesignationArg() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":smile:");
        argMap.put("username", "Test-sendWithDesignationArg");

        Destination destination = new Destination("test", "slack", argMap);
        Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        Notification notification = newNotification("test-arg", "Designation args test", attachments, exceptionInfo, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, customTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    public void sendWithTemplatePath() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":octopus:");
        argMap.put("username", "Test-sendWithTemplatePath");
        argMap.put("templatePath", "classpath:/tioga-notify-processor-slack/slack-template-debug.html");

        Destination destination = new Destination("test", "slack", argMap);
        Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        Notification notification = newNotification("test-arg", "Designation args test", attachments, exceptionInfo, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, customTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    public void sendWithSpecialChars() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":monkey_face:");
        argMap.put("username", "Test-sendWithSpecialChars");
        Destination destination = new Destination("test", "slack", argMap);
        Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        Notification notification = newNotification("test-special", "I didn't know you couldn't include apostrophes.", Collections.emptyList(), exceptionInfo, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, customTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    public void sendWithComplexStatus() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":rabbit:");
        argMap.put("username", "Test-sendWithComplexStatus");
        Destination destination = new Destination("test", "slack", argMap);
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

    public void sendWithSimpleStatus() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":crab:");
        argMap.put("username", "Test-sendWithSimpleStatus");
        Destination destination = new Destination("test", "slack", argMap);
        Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        String message = String.format("%s server:\n" +
                "  *  Runtime Dir     (solutions.runtime.dir)     %s\n" +
                "  *  Config Dir      (solutions.config.dir)      %s\n" +
                "  *  Logback File    (solutions.log.config)      %s\n" +
                "  *  Spring Path     (solutions.spring.config)   %s\n" +
                "  *  Active Profiles (solutions.active.profiles) %s", "started", null, null, null, null, asList("unit-test"));
        Notification notification = newNotification("test-special", message, Collections.emptyList(), null, Collections.emptyMap());

        TaskResponse response = processor.processTask(newDomainProfile(), notification, customTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    public void sendWithReallySimpleStatus() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":tropical_fish:");
        argMap.put("username", "Test-sendWithReallySimpleStatus");
        Destination destination = new Destination("test", "slack", argMap);
        Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        String message = "I think this is really going to work!";
        Notification notification = newNotification("test-special", message, Collections.emptyList(), null, Collections.emptyMap());

        TaskResponse response = processor.processTask(newDomainProfile(), notification, customTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    public void sendWithAttachmentsButNoException() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":hamster:");
        argMap.put("username", "Test-sendWithAttachmentsButNoException");
        Destination destination = new Destination("test", "slack", argMap);
        Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        String message = "I think this is really going to work!";
        Notification notification = newNotification("test-special", message, attachments, null, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, customTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
    }

    public void sendNoSlackUrlArg() {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("channel", "#notify-test");
        Destination destination = new Destination("test", "slack", argMap);
        Task badChannelTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        Notification notification = newNotification("test-bad-channel", "Base channel", attachments, exceptionInfo, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, badChannelTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.FAIL);
    }

    public void sendBadChannel() {
        Destination destination = new Destination("test", "slack", "channel:notify", "slackUrl:https://hooks.slack.com/services/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");
        Task badChannelTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        Notification notification = newNotification("test-bad-channel", "Base channel", attachments, exceptionInfo, traitMap);

        TaskResponse response = processor.processTask(newDomainProfile(), notification, badChannelTask);
        assertEquals(response.getResponseAction(), TaskResponseAction.FAIL);
        assertEquals(response.getMessage(), "Failure sending Slack message [404]: channel_not_found");

    }

    public void simpleMessageJsonTest() {
        SlackMessage message = new SlackMessage()
                .setChannel("#notify-test")
                .setIconEmoji(":smile:")
                .setUsername("Notifier")
                .setIconUrl("http://example.com/some-icon.ico")
                .setText("This is a message");

        String expected = "{\n" +
                "  \"username\" : \"Notifier\",\n" +
                "  \"channel\" : \"#notify-test\",\n" +
                "  \"text\" : \"This is a message\",\n" +
                "  \"icon_url\" : \"http://example.com/some-icon.ico\",\n" +
                "  \"icon_emoji\" : null\n" +
                "}";

        assertEquals(translator.toJson(message), expected);

        message = new SlackMessage()
                .setChannel("#notify-test")
                .setUsername("Notifier")
                .setIconUrl("http://example.com/some-icon.ico")
                .setIconEmoji(":smile:")
                .setText("This is a message");

        expected = "{\n" +
                "  \"username\" : \"Notifier\",\n" +
                "  \"channel\" : \"#notify-test\",\n" +
                "  \"text\" : \"This is a message\",\n" +
                "  \"icon_url\" : null,\n" +
                "  \"icon_emoji\" : \":smile:\"\n" +
                "}";
        assertEquals(translator.toJson(message), expected);
    }

    public void funkyMessageJsonTest() {
        SlackMessage message = new SlackMessage()
                .setChannel("#notify-test")
                .setIconEmoji(":smile:")
                .setUsername("Notifier")
                .setIconUrl("http://example.com/some-icon.ico")
                .setText("\nstuff<><\\>");

        String expected = "{\n" +
                "  \"username\" : \"Notifier\",\n" +
                "  \"channel\" : \"#notify-test\",\n" +
                "  \"text\" : \"\\nstuff<><\\\\>\",\n" +
                "  \"icon_url\" : \"http://example.com/some-icon.ico\",\n" +
                "  \"icon_emoji\" : null\n" +
                "}";
        assertEquals(translator.toJson(message), expected);
    }

    private DomainProfile newDomainProfile() {
        return new DomainProfile(
                "777", "r-3", "TestDomain", DomainStatus.ACTIVE,
                "some-api-key", "some-api-passowrd",
                "notification-db", "request-db",
                new RouteCatalog(Collections.emptyList(), Collections.emptyList())
        );
    }

    private Notification newNotification(String topic, String summary, List<AttachmentInfo> attachments, ExceptionInfo exceptionInfo, Map<String,String> traitMap) {

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

    public void testMessageBuilder() throws Exception {

        Map<String, String> argMap = new HashMap<>();
        argMap.put("slackUrl", tiogaSlackTestUrl);
        argMap.put("channel", "#notify-test");
        argMap.put("iconEmoji", ":smile:");
        argMap.put("Test-testMessageBuilder", "notifier");
        Map<String, String> argValueMap = new LinkedHashMap<>(argMap);

        Destination destination = new Destination("test", "slack", argMap);
        Task task = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

        Notification notification = newNotification("test-bad-channel", "Base channel", attachments, exceptionInfo, traitMap);

        ThymeleafMessageBuilder messageBuilder = new ThymeleafMessageBuilder();
        String templatePath = messageBuilder.getTemplatePath(argValueMap, "templatePath", SlackTaskProcessor.DEFAULT_TEMPLATE_PATH);
        String messageText = messageBuilder.createMessage(newDomainProfile(), notification, task, templatePath);

        // HACK - hn
/*
    String createdAt = notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a"));
    String expected = String.format("<http://localhost/some-task|Base channel>\\nTopic: test-bad-channel\\nCreated: %s\\nException: Opps, I tripped.", createdAt);

    Assert.assertEquals(messageText, expected);
*/
    }

    public String readResource(String resourcePath) {
        URL url = getClass().getClassLoader().getResource(resourcePath);
        if (url == null) {
            String msg = String.format("Unable to find file at: %s", resourcePath);
            throw ApiException.badRequest(msg);
        }
        try {
            Path path = Paths.get(url.toURI());
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (URISyntaxException | IOException e) {
            throw ApiException.internalServerError(e);
        }

    }

}
