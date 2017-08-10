package org.tiogasolutions.notify.kernel.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.notification.NotificationDomain;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.tiogasolutions.notify.pub.route.Destination;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.String.format;

@Component
public class TaskGenerator {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus;
    private final ExecutorService executorService;

    @Autowired
    public TaskGenerator(EventBus eventBus) {
        this.eventBus = eventBus;
        this.executorService = Executors.newCachedThreadPool();
    }

    @PreDestroy
    public void dispose() {
        executorService.shutdown();
    }

    // NOTE - we pass in notificationDomain because we already have it when we make this call
    public Future<List<TaskEntity>> generateTasks(NotificationDomain notificationDomain, Notification notification) {
        return executorService.submit( () -> createTask(notificationDomain, notification));
    }

    private List<TaskEntity> createTask(NotificationDomain notificationDomain, Notification notification) {

        log.error("Generating tasks for notification {}", notification.getNotificationId());
        List<TaskEntity> tasks = new ArrayList<>();

        try {
            // Find destinations.
            NotificationRef notificationRef = notification.toNotificationRef();
            Set<Destination> destinations = notificationDomain.findDestinations(notification);
            log.error("Found {} destinations", destinations.size());

            for (Destination destination : destinations) {
                createTask(notificationDomain, notification, tasks, notificationRef, destination);
            }

            log.error("Created {} tasks.", tasks.size());

        } catch (Exception e) {
            log.error(format("Exception generating tasks (notification=%s).", notification.getNotificationId()), e);
        }

        return tasks;
    }

    private void createTask(NotificationDomain notificationDomain, Notification notification, List<TaskEntity> tasks, NotificationRef notificationRef, Destination destination) {
        try {
            log.error("Creating task (notification={}, destination={})", notification.getNotificationId(), destination.getName());

            CreateTask create = CreateTask.create(notificationRef, destination);
            TaskEntity task = notificationDomain.createTask(create, notification);
            tasks.add(task);

            // log.error("Signalling creation of the task (notification={}, destination={}, task={})", notification.getNotificationId(), task.getTaskId(), destination);
            // eventBus.taskCreated(notificationDomain.getDomainName(), task, notification);

        } catch (Exception e) {
            String msg = format("Exception generating task (notification=%s, destination=%s).", notification.getNotificationId(), destination.getName());
            log.error(msg, e);
        }
    }
}
