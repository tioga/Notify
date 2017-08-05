package org.tiogasolutions.notify.kernel.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.notification.NotificationDomain;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.task.TaskStatus;
import org.tiogasolutions.notify.test.AbstractSpringTest;

import java.util.List;
import java.util.concurrent.Future;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

// HACK - disabled because we no longer have the hack in routing, need another way to test.
@Test(enabled = false)
public class TaskGeneratorTest extends AbstractSpringTest {

    @Autowired
    private TaskGenerator taskGenerator;

    @Autowired
    private TestFactory testFactory;

    @Autowired
    private DomainKernel domainKernel;

    @Autowired
    private ExecutionManager executionManager;

    private ExecutionContext executionContext;

    private NotificationDomain notificationDomain;

    @BeforeMethod
    public void beforeMethod() {
/*
    System.getProperties().put("HACK_EMAIL_RECIPIENT", "mickey.mouse@disney.com");
    System.getProperties().put("HACK_GTALK_RECIPIENT", "mickey.mouse@gmail.com");
    System.getProperties().put("HACK_SMS_RECIPIENT",   "1234567890");
*/

        executionManager.newApiContext(TestFactory.API_KEY);
        executionContext = executionManager.context();
        notificationDomain = domainKernel.notificationDomain(executionContext);
    }

    @AfterMethod
    public void afterMethod() {
        executionManager.clearContext();
    }

    public void testGenerateTasks_WithException() throws Exception {
        // HACK - this is directly tied to the hack in the NotificationDomain.findActiveRoutes()
        CreateNotification create = testFactory.newCreateNotificationWithException();
        Notification notification = testFactory.newNotification(executionContext, create).toNotification();

        Future<List<TaskEntity>> future = taskGenerator.generateTasks(notificationDomain, notification);

        // TODO consider using wait - future.wait();
        while (future.isDone() == false) {
            Thread.sleep(100);
        }

        List<TaskEntity> tasks = future.get();

        assertEquals(tasks.size(), 4);
        validateTask(notification, tasks.get(0), "emailMsg", "mickey.mouse@disney.com");
        validateTask(notification, tasks.get(1), "emailMsg", "test@jacobparr.com");
        validateTask(notification, tasks.get(2), "jabberMsg", "mickey.mouse@gmail.com");
        validateTask(notification, tasks.get(3), "smsMsg", "1234567890");
    }

    public void testGenerateTasks_NoException() throws Exception {
        // HACK - this is directly tied to the hack in the NotificationDomain.findActiveRoutes()
        CreateNotification create = testFactory.newCreateNotificationNoException();
        Notification notification = testFactory.newNotification(executionContext, create).toNotification();

        Future<List<TaskEntity>> future = taskGenerator.generateTasks(notificationDomain, notification);

        while (future.isDone() == false) {
            Thread.sleep(100);
        }

        List<TaskEntity> tasks = future.get();

        assertEquals(tasks.size(), 1);
        validateTask(notification, tasks.get(0), "jabberMsg", "mickey.mouse@gmail.com");
    }

    private void validateTask(Notification notification, TaskEntity task, String type, String recipient) {
        assertNotNull(task);
        assertNotNull(task.getTaskId());
        Destination destination = task.getDestination();
        assertEquals(destination.getArguments().get("recipient"), recipient);
        assertEquals(destination.getArguments().get("type"), type);
        assertEquals(task.getNotificationId(), notification.getNotificationId());
        assertEquals(task.getTaskStatus(), TaskStatus.PENDING);
    }
}