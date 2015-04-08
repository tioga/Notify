package org.lqnotify.kernel.notification;

import org.lqnotify.pub.Notification;
import org.lqnotify.pub.NotificationRef;
import org.lqnotify.pub.Task;
import org.lqnotify.pub.TaskStatus;

/**
 * Created by jacobp on 2/26/2015.
 */
public class TaskQuery {

  // By ID is expected to throw an exception which is counter
  // intuitive to how a Query object would work. Disabled for now.
  // private String taskId;

  private String notificationId;
  private TaskStatus taskStatus;
  private String destinationName;
  private String destinationProvider;
  private int offset = 0;
  private int limit = 100;

  public TaskQuery() {
  }

  public int getLimit() {
    return limit;
  }

  public TaskQuery setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  public int getOffset() {
    return offset;
  }

  public TaskQuery setOffset(int offset) {
    this.offset = offset;
    return this;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public TaskQuery setNotificationId(String notificationId) {
    this.notificationId = notificationId;
    return this;
  }

  public TaskStatus getTaskStatus() {
    return taskStatus;
  }

  public TaskQuery setTaskStatus(TaskStatus taskStatus) {
    this.taskStatus = taskStatus;
    return this;
  }

  public String getDestinationName() {
    return destinationName;
  }

  public TaskQuery setDestinationName(String destinationName) {
    this.destinationName = destinationName;
    return this;
  }

  public String getDestinationProvider() {
    return destinationProvider;
  }

  public TaskQuery setDestinationProvider(String destinationProvider) {
    this.destinationProvider = destinationProvider;
    return this;
  }

}
