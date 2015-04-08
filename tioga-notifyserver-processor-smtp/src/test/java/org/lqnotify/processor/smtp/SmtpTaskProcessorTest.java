package org.lqnotify.processor.smtp;

import org.tiogasolutions.dev.common.BeanUtils;
import org.lqnotify.kernel.processor.HtmlMessage;
import org.lqnotify.pub.*;
import org.lqnotify.pub.route.ArgValueMap;
import org.lqnotify.pub.route.Destination;
import org.lqnotify.pub.route.DestinationStatus;
import org.lqnotify.pub.route.RouteCatalog;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.testng.Assert.assertEquals;

@Test
public class SmtpTaskProcessorTest {

  private ArgValueMap argValueMap;
  private SmtpTaskProcessor processor = new SmtpTaskProcessor();

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
    argValueMap = new ArgValueMap(argHashMap);
  }

  public void testCreateEmailMessage() throws Exception {
    EmailMessage emailMessage = processor.createEmailMessage(argValueMap);
    assertEquals(emailMessage.getHost(), "mail.example.com");
    assertEquals(emailMessage.getPort(), "587");
    assertEquals(emailMessage.getUsername(), "mickey.mail");
    assertEquals(emailMessage.getPassword(), "my-little-secret");
    assertEquals(emailMessage.getFrom(), "goofy@disney.com");
    assertEquals(emailMessage.getTo(), Arrays.asList("mickey.mouse@disney.com", "minnie.mouse@disney.com"));
  }
}