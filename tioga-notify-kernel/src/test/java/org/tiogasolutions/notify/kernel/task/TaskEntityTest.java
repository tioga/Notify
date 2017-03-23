package org.tiogasolutions.notify.kernel.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.task.TaskStatus;
import org.tiogasolutions.notify.test.AbstractSpringTest;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class TaskEntityTest extends AbstractSpringTest {
    @Autowired
    private TestFactory testFactory;
    @Autowired
    private ExecutionManager executionManager;
    @Autowired
    private NotificationKernel notificationKernel;

    @BeforeMethod
    public void beforeMethod() {
        executionManager.newApiContext(TestFactory.API_KEY);
    }

    @AfterMethod
    public void afterMethod() {
        executionManager.clearContext();
    }

    public void testPersistence() throws Exception {

        CreateNotification create = testFactory.newCreateNotificationWithException();
        Notification notification = testFactory.newNotification(create);
        TaskEntity task = testFactory.newEmailTaskEntity(notification);

        String documentId = task.getTaskId();

        assertNotNull(documentId);
        assertNotNull(task.getRevision());
        Destination destination = task.getDestination();
        assertEquals(task.getNotificationId(), notification.getNotificationId());
        assertEquals(task.getTaskStatus(), TaskStatus.PENDING);
        Map<String, String> argMap = destination.getArguments();
        assertEquals(argMap.get("recipient"), "test@jacobparr.com");
        assertEquals(argMap.get("type"), "emailMsg");

        task.sending();

        String revision = task.getRevision();
        task = notificationKernel.saveAndReload(task);
        destination = task.getDestination();
        assertEquals(task.getTaskId(), documentId);
        Assert.assertNotEquals(task.getRevision(), revision);
        assertEquals(task.getNotificationId(), notification.getNotificationId());
        assertEquals(task.getTaskStatus(), TaskStatus.SENDING);
        argMap = destination.getArguments();
        assertEquals(argMap.get("recipient"), "test@jacobparr.com");
        assertEquals(argMap.get("type"), "emailMsg");

        revision = task.getRevision();
        task = notificationKernel.findTaskById(documentId);
        destination = task.getDestination();

        assertEquals(task.getTaskId(), documentId);
        Assert.assertEquals(task.getRevision(), revision);
        assertEquals(task.getNotificationId(), notification.getNotificationId());
        assertEquals(task.getTaskStatus(), TaskStatus.SENDING);
        argMap = destination.getArguments();
        assertEquals(argMap.get("recipient"), "test@jacobparr.com");
        assertEquals(argMap.get("type"), "emailMsg");
    }
}