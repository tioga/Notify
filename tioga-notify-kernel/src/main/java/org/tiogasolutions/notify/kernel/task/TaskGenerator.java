package org.tiogasolutions.notify.kernel.task;

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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class TaskGenerator {

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

        Callable<List<TaskEntity>> taskCreatorCallable = () -> {
            List<TaskEntity> tasks = new ArrayList<>();

            // Find destinations.
            NotificationRef notificationRef = notification.toNotificationRef();
            Set<Destination> destinations = notificationDomain.findDestinations(notification);

            for (Destination destination : destinations) {
                // Create the task.
                CreateTask create = CreateTask.create(notificationRef, destination);
                TaskEntity task = notificationDomain.createTask(create, notification);
                tasks.add(task);
            }
            return tasks;
        };

        return executorService.submit(taskCreatorCallable);
    }

}
