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
import org.tiogasolutions.notify.pub.route.DestinationStatus;
import org.tiogasolutions.notify.test.AbstractSpringTest;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;
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
    private MockPushClientFactory pushClientFactory;

    @BeforeMethod
    public void beforeMethod() {
        executionManager.newApiContext(TestFactory.API_KEY);
    }

    @AfterMethod
    public void afterMethod() {
        executionManager.clearContext();
    }

    private Destination createDestination(String url, String from, String type, String recipient) {
        return new DestinationDef("local", DestinationStatus.ENABLED, "push",
                "url:" + url,
                "from:" + from,
                "type:" + type,
                "recipients:" + recipient)
                .toDestination();
    }

    @Test(expectedExceptions = UnsupportedMethodException.class)
    public void testProcessTask_PHONE() throws Exception {
        CreateNotification createNotification = testFactory.newCreateNotificationWithException();
        DomainProfile domainProfile = testFactory.newDomainProfile();
        Notification notification = testFactory.newNotification(createNotification);

        CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("http://example.com", "5591234567", "phoneCall", "1234567890"));
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

        CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("http://example.com", "mickey.mouse@disney.com", "jabberMsg", "test@jacobparr.com"));
        TaskEntity task = TaskEntity.newEntity(createTask);
        processor.processTask(domainProfile, notification, task.toTask());
        assertNotNull(pushClientFactory.getLastClient().getLastPush());
        assertEquals(pushClientFactory.getLastClient().getLastPush().getClass(), XmppPush.class);
        assertEquals(((XmppPush) pushClientFactory.getLastClient().getLastPush()).getRecipient(), "test@jacobparr.com");
    }

    public void testProcessTask_SMS() throws Exception {
        CreateNotification createNotification = testFactory.newCreateNotificationWithException();
        DomainProfile domainProfile = testFactory.newDomainProfile();
        Notification notification = testFactory.newNotification(createNotification);

        CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("http://example.com", "5591234567", "smsMsg", "1234567890"));
        TaskEntity task = TaskEntity.newEntity(createTask);
        processor.processTask(domainProfile, notification, task.toTask());
        assertNotNull(pushClientFactory.getLastClient().getLastPush());
        assertEquals(pushClientFactory.getLastClient().getLastPush().getClass(), TwilioSmsPush.class);
        assertEquals(((TwilioSmsPush) pushClientFactory.getLastClient().getLastPush()).getRecipient(), "1234567890");
    }

    public void testProcessTask_SMTP_EMAIL() throws Exception {
        CreateNotification createNotification = testFactory.newCreateNotificationWithException();
        DomainProfile domainProfile = testFactory.newDomainProfile();
        Notification notification = testFactory.newNotification(createNotification);

        CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("http://example.com", "mickey.mouse@disney.com", "smtpEmailMsg", "mickey.mouse@disney.com"));
        TaskEntity task = TaskEntity.newEntity(createTask);
        processor.processTask(domainProfile, notification, task.toTask());
        assertNotNull(pushClientFactory.getLastClient().getLastPush());
        assertEquals(pushClientFactory.getLastClient().getLastPush().getClass(), SmtpEmailPush.class);
        assertEquals(((SmtpEmailPush) pushClientFactory.getLastClient().getLastPush()).getToAddress(), "mickey.mouse@disney.com");
    }

    public void testProcessTask_SES_EMAIL() throws Exception {
        CreateNotification createNotification = testFactory.newCreateNotificationWithException();
        DomainProfile domainProfile = testFactory.newDomainProfile();
        Notification notification = testFactory.newNotification(createNotification);

        CreateTask createTask = CreateTask.create(notification.toNotificationRef(), createDestination("http://example.com", "mickey.mouse@disney.com", "sesEmailMsg", "mickey.mouse@disney.com"));
        TaskEntity task = TaskEntity.newEntity(createTask);
        processor.processTask(domainProfile, notification, task.toTask());
        assertNotNull(pushClientFactory.getLastClient().getLastPush());
        assertEquals(pushClientFactory.getLastClient().getLastPush().getClass(), SesEmailPush.class);
        assertEquals(((SesEmailPush) pushClientFactory.getLastClient().getLastPush()).getToAddress(), "mickey.mouse@disney.com");
    }
}