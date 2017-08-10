package org.tiogasolutions.notify.kernel.notification;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.common.exceptions.ApiBadRequestException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.route.JsRouteEvaluator;
import org.tiogasolutions.notify.kernel.route.RouteEvaluator;
import org.tiogasolutions.notify.kernel.task.CreateTask;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.kernel.task.TaskGenerator;
import org.tiogasolutions.notify.kernel.task.TaskStore;
import org.tiogasolutions.notify.pub.attachment.AttachmentHolder;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.pub.task.TaskQuery;

import java.util.Set;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 10:44 PM
 */
public class NotificationDomain {

    private final String domainName;
    private final TaskGenerator taskGenerator;
    private final EventBus eventBus;
    private final NotificationStore notificationStore;
    private final TaskStore taskStore;
    private final RouteEvaluator routeEvaluator;

    public NotificationDomain(String domainName,
                              CouchDatabase couchDatabase,
                              RouteCatalog routeCatalog,
                              TaskGenerator taskGenerator,
                              EventBus eventBus) {
        this.domainName = domainName;
        this.taskGenerator = taskGenerator;
        this.eventBus = eventBus;
        this.notificationStore = new NotificationStore(couchDatabase);
        this.taskStore = new TaskStore(couchDatabase);
        this.routeEvaluator = new JsRouteEvaluator(routeCatalog);
    }

    public String getDomainName() {
        return domainName;
    }

    public RouteEvaluator getRouteEvaluator() {
        return routeEvaluator;
    }

    public Set<Destination> findDestinations(Notification notification) {
        return getRouteEvaluator().findDestinations(notification);
    }

    public NotificationRef createNotification(CreateNotification create) {
        ExceptionUtils.assertNotNull(create, "create", ApiBadRequestException.class);

        NotificationEntity entity = NotificationEntity.newEntity(getDomainName(), create);

        Notification notification = notificationStore.saveAndReload(entity).toNotification();

        // Immediately pass the notification on to the task generate.
        taskGenerator.generateTasks(this, notification);

        return notification.toNotificationRef();
    }

    public NotificationEntity findNotificationById(String notificationId) {
        return notificationStore.findNotificationById(notificationId);
    }

    public QueryResult<Notification> query(NotificationQuery query) {
        return notificationStore.query(query);
    }

    public NotificationRef createAttachment(CreateAttachment create) {
        return notificationStore.createAttachment(create);
    }

    public AttachmentHolder findAttachment(String notificationId, String attachmentName) {
        return notificationStore.findAttachment(notificationId, attachmentName);
    }

    public void deleteNotification(String notificationId) {
        notificationStore.deleteNotification(notificationId);
    }

    public TaskEntity findTaskById(String entityId) {
        return taskStore.findTaskById(entityId);
    }

    public ListQueryResult<TaskEntity> query(TaskQuery query) {
        return taskStore.query(query);
    }

    public TaskEntity createTask(CreateTask create, Notification notification) {
        TaskEntity taskEntity = taskStore.createTask(create);
        eventBus.taskCreated(domainName, taskEntity, notification);
        return taskEntity;
    }

    public void save(TaskEntity entity) {
        taskStore.save(entity);
    }

    public TaskEntity saveAndReload(TaskEntity entity) {
        return taskStore.saveAndReload(entity);
    }

    public void deleteTask(String taskId) {
        taskStore.deleteTask(taskId);
    }

}
