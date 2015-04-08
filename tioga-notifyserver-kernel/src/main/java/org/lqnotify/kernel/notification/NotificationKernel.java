package org.lqnotify.kernel.notification;

import org.crazyyak.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.execution.ExecutionAccessor;
import org.lqnotify.kernel.execution.ExecutionContext;
import org.lqnotify.kernel.task.CreateTask;
import org.lqnotify.kernel.task.TaskEntity;
import org.lqnotify.pub.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.ZoneId;
import java.util.List;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 10:03 PM
 */
@Named
public class NotificationKernel {
  private static Logger log = LoggerFactory.getLogger(NotificationKernel.class);
  private final ExecutionAccessor executionAccessor;
  private final DomainKernel domainKernel;

  @Inject
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
