package org.tiogasolutions.notify.engine.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.notification.NotificationDomain;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.pub.task.TaskQuery;

import java.util.List;

import static java.lang.String.format;

public class PruneNotificationsAndTasksJob implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean running = false;
    private long notificationsProcessed = 0;
    private long tasksProcessed = 0;

    private final String domainName;
    private final NotificationDomain notificationDomain;

    public PruneNotificationsAndTasksJob(DomainKernel domainKernel, String domainName) {
        this.domainName = domainName;
        this.notificationDomain = domainKernel.notificationDomain(domainName);
    }

    public void run() {
        running = true;
        try {
            List<Notification> notifications = null;
            while (notifications == null || notifications.size() > 0) {
                NotificationQuery noteQuery = new NotificationQuery().setLimit(100);
                notifications = notificationDomain.query(noteQuery).getResults();
                log.info("Deleting {} notifications for the domain {}.", notifications.size(), domainName);

                for (Notification notification : notifications) {
                    pruneNotification(notification);
                }
            }

            // Now it is completely possible that there are tasks out there that are
            // orphaned - their notification doesn't exist. Let's take them out next...
            List<TaskEntity> tasks = null;
            while (tasks == null || tasks.size() > 0) {
                tasks = notificationDomain.query(new TaskQuery().setLimit(100)).getResults();
                log.info("Deleting {} abandoned tasks for the domain {}.", tasks.size(), domainName);

                for (TaskEntity task : tasks) {
                    if (task.getTaskStatus().isCompleted() || task.getTaskStatus().isFailed()) {
                        tasksProcessed++;
                        notificationDomain.deleteTask(task.getTaskId());
                    }
                }
            }

        } catch (Exception e) {
            String msg = format("Exception deleting notifications & tasks for the domain %s.", domainName);
            log.error(msg, e);

        } finally {
            running = false;
            log.info("Finished pruning notifications & tasks for the domain {}.", domainName);
        }
    }

    protected void pruneNotification(Notification notification) {
        TaskQuery taskQuery = new TaskQuery().setNotificationId(notification.getNotificationId());
        List<TaskEntity> tasks = notificationDomain.query(taskQuery).getResults();

        // Test the tasks - if any are sending or pending skip everything.
        for (TaskEntity task : tasks) {
            if (task.getTaskStatus().isSending() || task.getTaskStatus().isPending()) {
                log.info("Skipping {} tasks given notification {} for the domain {}.", tasks.size(), notification.getNotificationId(), domainName);
                return;
            }
        }

        // OK, no issues, so delete all the tests.
        log.info("Deleting {} tasks given notification {} for the domain {}.", tasks.size(), notification.getNotificationId(), domainName);
        for (TaskEntity task : tasks) {
            tasksProcessed++;
            notificationDomain.deleteTask(task.getTaskId());
        }

        // And lastly, delete the notification
        notificationsProcessed++;
        notificationDomain.deleteNotification(notification.getNotificationId());
    }

    public boolean isRunning() {
        return running;
    }

    public Results getResults() {
        if (running) {
            return new Results(notificationsProcessed, tasksProcessed, String.format("RUNNING: Deleted %s notifications and %s tasks from the domain %s.", notificationsProcessed, tasksProcessed, domainName));
        } else {
            return new Results(notificationsProcessed, tasksProcessed, String.format("COMPLETED: Deleted %s notifications and %s tasks from the domain %s.", notificationsProcessed, tasksProcessed, domainName));
        }
    }

    public static class Results {

        private final String message;
        private final long notificationsProcessed;
        private final long tasksProcessed;

        public Results(long notificationsProcessed, long tasksProcessed, String message) {
            this.tasksProcessed = tasksProcessed;
            this.notificationsProcessed = notificationsProcessed;
            this.message = message;
        }

        public long getNotificationsProcessed() {
            return notificationsProcessed;
        }

        public long getTasksProcessed() {
            return tasksProcessed;
        }

        public String getMessage() {
            return message;
        }
    }
}
