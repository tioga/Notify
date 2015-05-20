package org.tiogasolutions.notify.processor.slack;

import org.tiogasolutions.notify.pub.*;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.kernel.processor.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.pub.route.ArgValueMap;
import org.tiogasolutions.notify.pub.route.Destination;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by harlan on 3/9/15.
 */
@Test(enabled = false)
public class SlackTaskProcessorTest {

  private final SlackTaskProcessor processor;
  private final URI someUri = URI.create("http://localhost/some-task");
  private final String tiogaSlackTestUrl = "https://hooks.slack.com/services/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";

  public SlackTaskProcessorTest() {
    processor = new SlackTaskProcessor();

  }

  public void sendDefaultMessage() {
    Map<String, String> argMap = new HashMap<>();
    argMap.put("slackUrl", tiogaSlackTestUrl);
    Destination destination = new Destination("test", "slack", argMap);

    Task task = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);
    Notification notification = newNotification("test-default", "Default test notification");

    DomainProfile domainProfile = newDomainProfile();
    TaskResponse response = processor.processTask(domainProfile, notification, task);
    assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
  }

  public void sendWithDesignationArg() {
    Map<String, String> argMap = new HashMap<>();
    argMap.put("slackUrl", tiogaSlackTestUrl);
    argMap.put("channel", "#notify-test");
    argMap.put("iconEmoji", ":smile:");
    argMap.put("userName", "notifier");
    Destination destination = new Destination("test", "slack", argMap);
    Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

    DomainProfile domainProfile = newDomainProfile();
    Notification notification = newNotification("test-arg", "Designation args test");
    TaskResponse response = processor.processTask(domainProfile, notification, customTask);
    assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
  }

  public void sendWithTemplatePath() {
    Map<String, String> argMap = new HashMap<>();
    argMap.put("slackUrl", tiogaSlackTestUrl);
    argMap.put("channel", "#notify-test");
    argMap.put("iconEmoji", ":smile:");
    argMap.put("userName", "notifier");
    argMap.put("templatePath", "file:/dvlp/tioga/notify/runtime/config/templates/slack-template-debug.html");
    Destination destination = new Destination("test", "slack", argMap);
    Task customTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

    DomainProfile domainProfile = newDomainProfile();
    Notification notification = newNotification("test-arg", "Designation args test");
    TaskResponse response = processor.processTask(domainProfile, notification, customTask);
    assertEquals(response.getResponseAction(), TaskResponseAction.COMPLETE);
  }

  public void sendNoSlackUrlArg() {
    Map<String, String> argMap = new HashMap<>();
    argMap.put("channel", "#notify-test");
    Destination destination = new Destination("test", "slack", argMap);
    Task badChannelTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

    DomainProfile domainProfile = newDomainProfile();
    Notification notification = newNotification("test-bad-channel", "Base channel");
    TaskResponse response = processor.processTask(domainProfile, notification, badChannelTask);
    assertEquals(response.getResponseAction(), TaskResponseAction.FAIL);
  }

  public void sendBadChannel() {
    Map<String, String> argMap = new HashMap<>();
    argMap.put("channel", "notify");
    Destination destination = new Destination("test", "slack", argMap);
    Task badChannelTask = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);

    DomainProfile domainProfile = newDomainProfile();
    Notification notification = newNotification("test-bad-channel", "Base channel");
    TaskResponse response = processor.processTask(domainProfile, notification, badChannelTask);
    assertEquals(response.getResponseAction(), TaskResponseAction.FAIL);

  }

  public void messageJsonTest() {
    SlackMessage message = new SlackMessage()
        .setChannel("#notify-test")
        .setIconEmoji(":smile:")
        .setUserName("Notifier")
        .setIconUrl("")
        .setText("This is a message");

    // TODO - improve this test.
    assertNotNull(message.toJson());
  }

  private DomainProfile newDomainProfile() {
    return new DomainProfile(
      "777", "r-3", "TestDomain", DomainStatus.ACTIVE,
      "some-api-key", "some-api-passowrd",
      "notification-db", "request-db",
      new RouteCatalog(Collections.emptyList(), Collections.emptyList())
    );
  }

  private Notification newNotification(String topic, String summary) {
    return new Notification(someUri,
        "999",
        "888",
        null,
        topic,
        summary,
        "track-9999",
        ZonedDateTime.now(),
        null,
        new ExceptionInfo(new RuntimeException("Opps, I tripped.")),
        null);
  }

  public void testMessageBuilder() throws Exception {

    Map<String, String> argMap = new HashMap<>();
    argMap.put("slackUrl", tiogaSlackTestUrl);
    argMap.put("channel", "#notify-test");
    argMap.put("iconEmoji", ":smile:");
    argMap.put("userName", "notifier");
    ArgValueMap argValueMap = new ArgValueMap(argMap);

    Destination destination = new Destination("test", "slack", argMap);
    Task task = new Task(someUri, null, null, TaskStatus.SENDING, "9999", ZonedDateTime.now(), destination, null);
    DomainProfile domainProfile = newDomainProfile();
    Notification notification = newNotification("test-bad-channel", "Base channel");

    ThymeleafMessageBuilder messageBuilder = new ThymeleafMessageBuilder();
    String templatePath = messageBuilder.getTemplatePath(argValueMap, "templatePath", SlackTaskProcessor.DEFAULT_TEMPLATE_PATH);
    String messageText = messageBuilder.createMessage(domainProfile, notification, task, templatePath);

    // HACK - hn
/*
    String createdAt = notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a"));
    String expected = String.format("<http://localhost/some-task|Base channel>\\nTopic: test-bad-channel\\nCreated: %s\\nException: Opps, I tripped.", createdAt);

    Assert.assertEquals(messageText, expected);
*/
  }
}
