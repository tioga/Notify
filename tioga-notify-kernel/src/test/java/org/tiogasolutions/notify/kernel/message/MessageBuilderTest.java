package org.tiogasolutions.notify.kernel.message;

import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.notify.kernel.message.HtmlMessage;
import org.tiogasolutions.notify.kernel.message.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.pub.*;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.DestinationStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
    Map<String,String> argHashMap = BeanUtils.toMap(
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
      URI.create("http://whatever.com/api/v1/notifications/123"),
      "some-domain", "123", "r-1", "test-topic",
      "Something really bad just happened.", "tracking id #321",
      ZonedDateTime.now(),
      BeanUtils.toMap("color:red", "size:medium"),
      new ExceptionInfo(new RuntimeException("Oops, I tripped.")),
      Arrays.asList(new AttachmentInfo("screenshot.png", "image/png")));

    task = new Task(
        URI.create("http://whatever.com/api/v1/tasks/456"),
        "456",
        "r-9",
        TaskStatus.SENDING,
        "123",
        ZonedDateTime.now(),
        new Destination("E-Mails", "smtp", DestinationStatus.ENABLED, argHashMap),
        null);
  }

  public void testCreateHtmlContent() throws Exception {
    String templatePath = getTemplate();
    HtmlMessage message = messageBuilder.createHtmlMessage(domainProfile, notification, task, templatePath);

    Assert.assertTrue(message.getHtml().contains("<html"));
    Assert.assertTrue(message.getHtml().contains("<title>NOTIFICATION: Something really bad just happened.</title>"));

    // I shouldn't have any of the tags outside or including the body tag.
    Assert.assertFalse(message.getBody().contains("<html>"));
    Assert.assertFalse(message.getBody().contains("<head>"));
    Assert.assertFalse(message.getBody().contains("<title>"));
    Assert.assertFalse(message.getBody().contains("<body>"));
    // But I should have the content of the body.
    Assert.assertTrue(message.getBody().contains("<li>Topic: <span>test-topic</span></li>"));
    String createdAt = notification.getCreatedAt().format(DateTimeFormatter.ofPattern("MM-dd-yy hh:mm a"));
    Assert.assertTrue(message.getBody().contains("<li>Created: <span>" + createdAt + "</span></li>"));

    // Lastly verify the subject
    Assert.assertEquals(message.getSubject(), "NOTIFICATION: Something really bad just happened.");
  }

  private String getTemplate() throws Exception {
    File file = new File("").getAbsoluteFile();

    if (file.getAbsolutePath().endsWith("tioga-notify-kernel")) {
      file = new File(file, "../runtime/config/templates/email-template.html");
    } else {
      file = new File(file, "/runtime/config/templates/email-template.html");
    }

    if (file.exists() == false) {
      throw new FileNotFoundException("The template file does not exist: "+ file.getAbsolutePath());
    }

    return "file:" + file.getAbsolutePath();
  }
}