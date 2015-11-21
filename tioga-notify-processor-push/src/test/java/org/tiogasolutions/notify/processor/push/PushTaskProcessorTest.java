package org.tiogasolutions.notify.processor.push;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.exceptions.UnsupportedMethodException;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.task.CreateTask;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.DestinationDef;
import org.tiogasolutions.notify.test.AbstractSpringTest;
import org.tiogasolutions.push.client.MockPushServerClient;
import org.tiogasolutions.push.pub.EmailPush;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.XmppPush;

import static org.testng.Assert.*;

@Test
public class PushTaskProcessorTest extends AbstractSpringTest {

  @Autowired
  private TestFactory testFactory;

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  private PushTaskProcessor processor;

  @Autowired
  private MockPushServerClient pushServerClient;

  @BeforeMethod
  public void beforeMethod() {
    executionManager.newApiContext(TestFactory.API_KEY);
  }

  @AfterMethod
  public void afterMethod() {
    executionManager.clearContext();
  }

  private Destination createDestination(String type, String recipient) {
    return new DestinationDef("local", "push")
        .addArg("type", type)
        .addArg("recipient", recipient)
        .toDestination();
  }

  @Test(expectedExceptions = UnsupportedMethodException.class)
  public void testProcessTask_PHONE() throws Exception {
    CreateNotification createNotification = testFactory.newCreateNotificationWithException();
    DomainProfile domainProfile = testFactory.newDomainProfile();
    Notification notification = testFactory.newNotification(createNotification);

    CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("phoneCall",  "1234567890"));
    TaskEntity task = TaskEntity.newEntity(createTask);
    processor.processTask(domainProfile, notification, task.toTask());
    fail("Expected exception - not yet implemented.");
//    assertNotNull(gateway.lastPush);
//    assertEquals(gateway.lastPush.getClass(), GoogleTalkPush.class);
//    assertEquals(((GoogleTalkPush)gateway.lastPush).getRecipient(), "1234567890");
  }

  public void testProcessTask_JABBER() throws Exception {
    CreateNotification createNotification = testFactory.newCreateNotificationWithException();
    DomainProfile domainProfile = testFactory.newDomainProfile();
    Notification notification = testFactory.newNotification(createNotification);

    CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("jabberMsg",  "test@jacobparr.com"));
    TaskEntity task = TaskEntity.newEntity(createTask);
    processor.processTask(domainProfile, notification, task.toTask());
    assertNotNull(pushServerClient.getLastPush());
    assertEquals(pushServerClient.getLastPush().getClass(), XmppPush.class);
    assertEquals(((XmppPush)pushServerClient.getLastPush()).getRecipient(), "test@jacobparr.com");
  }

  public void testProcessTask_SMS() throws Exception {
    CreateNotification createNotification = testFactory.newCreateNotificationWithException();
    DomainProfile domainProfile = testFactory.newDomainProfile();
    Notification notification = testFactory.newNotification(createNotification);

    CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("smsMsg",  "1234567890"));
    TaskEntity task = TaskEntity.newEntity(createTask);
    processor.processTask(domainProfile, notification, task.toTask());
    assertNotNull(pushServerClient.getLastPush());
    assertEquals(pushServerClient.getLastPush().getClass(), TwilioSmsPush.class);
    assertEquals(((TwilioSmsPush)pushServerClient.getLastPush()).getRecipient(), "1234567890");
  }

  public void testProcessTask_EMAIL() throws Exception {
    CreateNotification createNotification = testFactory.newCreateNotificationWithException();
    DomainProfile domainProfile = testFactory.newDomainProfile();
    Notification notification = testFactory.newNotification(createNotification);

    CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("emailMsg",  "mickey.mouse@disney.com"));
    TaskEntity task = TaskEntity.newEntity(createTask);
    processor.processTask(domainProfile, notification, task.toTask());
    assertNotNull(pushServerClient.getLastPush());
    assertEquals(pushServerClient.getLastPush().getClass(), EmailPush.class);
    assertEquals(((EmailPush)pushServerClient.getLastPush()).getToAddress(), "mickey.mouse@disney.com");
  }
}