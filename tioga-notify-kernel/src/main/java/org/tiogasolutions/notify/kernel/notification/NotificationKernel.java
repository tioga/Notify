package org.tiogasolutions.notify.kernel.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionAccessor;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.task.CreateTask;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.attachment.AttachmentHolder;
import org.tiogasolutions.notify.pub.attachment.AttachmentQuery;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.tiogasolutions.notify.pub.task.TaskQuery;

import java.time.ZoneId;

@Component
public class NotificationKernel {
  private static Logger log = LoggerFactory.getLogger(NotificationKernel.class);
  private final ExecutionAccessor executionAccessor;
  private final DomainKernel domainKernel;

  @Autowired
  public NotificationKernel(ExecutionAccessor executionAccessor, DomainKernel domainKernel) {
    log.info("Default zoneId: " + ZoneId.systemDefault());

    this.executionAccessor = executionAccessor;
    this.domainKernel = domainKernel;
  }

  public NotificationRef createNotification(CreateNotification create) {
    return domain().createNotification(create);
  }

  public NotificationRef createAttachment(CreateAttachment create) {
    return domain().createAttachment(create);
  }

  public QueryResult<Notification> query(NotificationQuery query) {
    return domain().query(query);
  }

  /**
   * Finds a notification by it's specific ID.
   * @param notificationId the notification's ID
   * @return the requested notification
   * @throws ApiNotFoundException if the notification does not exists.
   */
  public Notification findNotificationById(String notificationId) throws ApiNotFoundException {
    return domain().findNotificationById(notificationId).toNotification();
  }

  public AttachmentHolder query(AttachmentQuery query) {
    return domain().findAttachment(query.getNotificationId(), query.getAttachmentName());
  }

  public void deleteNotification(String notificationId) {
    domain().deleteNotification(notificationId);
  }

  public QueryResult<TaskEntity> query(TaskQuery query) {
    return domain().query(query);
  }

  /**
   * Finds a task by it's specific ID.
   * @param taskId the task's ID
   * @return the requested task
   * @throws ApiNotFoundException if the notification does not exists.
   */
  public TaskEntity findTaskById(String taskId) throws ApiNotFoundException {
    return domain().findTaskById(taskId);
  }

  public TaskEntity createTask(CreateTask create, Notification notification) {
    return domain().createTask(create, notification);
  }

  public TaskEntity saveAndReload(TaskEntity taskEntity) {
    return domain().saveAndReload(taskEntity);
  }

  public void deleteTask(String taskId) {
    domain().deleteTask(taskId);
  }

  protected NotificationDomain domain() {
    ExecutionContext ec = executionAccessor.context();
    return domainKernel.notificationDomain(ec);
  }

  public void readAttachment(String notificationId, String attachmentName) {

  }
}
