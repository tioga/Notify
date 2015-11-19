package org.tiogasolutions.notify.processor.push;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
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
import org.tiogasolutions.push.pub.EmailPush;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.XmppPush;

import static org.testng.Assert.*;

@Test
public class PushTaskProcessorTest extends ProcessorPushAbstractTest implements BeanFactoryAware {

  @Autowired
  private TestFactory testFactory;

  @Autowired
  private ExecutionManager executionManager;

  private PushTaskProcessor processor;

  @Autowired
  private TestCosmicPushGateway gateway;

  @Override
  @Test(enabled = false) // I ain't no stinking test.
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    processor = new PushTaskProcessor();
    processor.init(beanFactory);
  }

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
    assertNotNull(gateway.lastPush);
    assertEquals(gateway.lastPush.getClass(), XmppPush.class);
    assertEquals(((XmppPush)gateway.lastPush).getRecipient(), "test@jacobparr.com");
  }

  public void testProcessTask_SMS() throws Exception {
    CreateNotification createNotification = testFactory.newCreateNotificationWithException();
    DomainProfile domainProfile = testFactory.newDomainProfile();
    Notification notification = testFactory.newNotification(createNotification);

    CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("smsMsg",  "1234567890"));
    TaskEntity task = TaskEntity.newEntity(createTask);
    processor.processTask(domainProfile, notification, task.toTask());
    assertNotNull(gateway.lastPush);
    assertEquals(gateway.lastPush.getClass(), TwilioSmsPush.class);
    assertEquals(((TwilioSmsPush)gateway.lastPush).getRecipient(), "1234567890");
  }

  public void testProcessTask_EMAIL() throws Exception {
    CreateNotification createNotification = testFactory.newCreateNotificationWithException();
    DomainProfile domainProfile = testFactory.newDomainProfile();
    Notification notification = testFactory.newNotification(createNotification);

    CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("emailMsg",  "mickey.mouse@disney.com"));
    TaskEntity task = TaskEntity.newEntity(createTask);
    processor.processTask(domainProfile, notification, task.toTask());
    assertNotNull(gateway.lastPush);
    assertEquals(gateway.lastPush.getClass(), EmailPush.class);
    assertEquals(((EmailPush)gateway.lastPush).getToAddress(), "mickey.mouse@disney.com");
  }
}