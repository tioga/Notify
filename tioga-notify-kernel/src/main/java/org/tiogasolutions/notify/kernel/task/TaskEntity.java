package org.tiogasolutions.notify.kernel.task;

import org.tiogasolutions.couchace.annotations.CouchEntity;
import org.tiogasolutions.couchace.annotations.CouchId;
import org.tiogasolutions.couchace.annotations.CouchRevision;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.notify.pub.task.TaskResponse;
import org.tiogasolutions.notify.pub.task.TaskRef;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@CouchEntity("Task")
public class TaskEntity {

  public static TaskEntity newEntity(CreateTask create) {
    return new TaskEntity(TimeUuid.randomUUID().toString(),
        null,
        TaskStatus.PENDING,
        create.getNotificationId(),
        ZonedDateTime.now(),
        create.getDestination(),
        null);
  }

  protected String taskId;
  protected TaskStatus taskStatus;
  protected String revision;
  protected String notificationId;
  protected ZonedDateTime createdAt;
  protected Destination destination;
  protected TaskResponse lastResponse;

  @JsonCreator
  private TaskEntity(@JsonProperty("taskId") String taskId,
                     @JsonProperty("revision") String revision,
                     @JsonProperty("taskStatus") TaskStatus taskStatus,
                     @JsonProperty("notificationId") String notificationId,
                     @JsonProperty("createdAt") ZonedDateTime createdAt,
                     @JsonProperty("destination") Destination destination,
                     @JsonProperty("lastResponse") TaskResponse lastResponse) {

    this.taskId = taskId;
    this.revision = revision;
    this.taskStatus = taskStatus;
    this.notificationId = notificationId;
    this.createdAt = createdAt;
    this.destination = destination;
    this.lastResponse = lastResponse;
  }

  public Task toTask() {
    // HACK - null uri info
    return new Task(null, taskId, revision, taskStatus, notificationId, createdAt, destination, lastResponse);
  }

  @CouchId
  public final String getTaskId() {
    return taskId;
  }

  @CouchRevision
  public final String getRevision() {
    return revision;
  }

  public final TaskStatus getTaskStatus() {
    return taskStatus;
  }

  public final String getNotificationId() {
    return notificationId;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public Destination getDestination() {
    return destination;
  }

  public final TaskEntity pending() {
    taskStatus = TaskStatus.PENDING;
    return this;
  }

  public final TaskEntity sending() {
    taskStatus = TaskStatus.SENDING;
    return this;
  }

  public final void response(TaskResponse response) {
    lastResponse = response;
    switch(response.getResponseAction()) {
      case RETRY:
        // TODO - implement retry count solution
        break;
      case COMPLETE:
        taskStatus = TaskStatus.COMPLETED;
        break;
      case FAIL:
        taskStatus = TaskStatus.FAILED;
        break;
    }
  }

  public TaskResponse getLastResponse() {
    return lastResponse;
  }

  public TaskRef toTaskRef() {
    return new TaskRef(taskId, revision, notificationId);
  }

  @JsonIgnore
  public String getLabel() {
    List<String> argValues = new ArrayList<>();
    for (String value : destination.getArguments().values()) {
      argValues.add(value);
    }
    return String.format("Task %s: %s %s", taskId, destination.getProvider(), argValues);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TaskEntity that = (TaskEntity) o;

    if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
    if (destination != null ? !destination.equals(that.destination) : that.destination != null) return false;
    if (lastResponse != null ? !lastResponse.equals(that.lastResponse) : that.lastResponse != null) return false;
    if (notificationId != null ? !notificationId.equals(that.notificationId) : that.notificationId != null)
      return false;
    if (revision != null ? !revision.equals(that.revision) : that.revision != null) return false;
    if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) return false;
    if (taskStatus != that.taskStatus) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = taskId != null ? taskId.hashCode() : 0;
    result = 31 * result + (taskStatus != null ? taskStatus.hashCode() : 0);
    result = 31 * result + (revision != null ? revision.hashCode() : 0);
    result = 31 * result + (notificationId != null ? notificationId.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    result = 31 * result + (destination != null ? destination.hashCode() : 0);
    result = 31 * result + (lastResponse != null ? lastResponse.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TaskEntity{" +
        "taskId='" + taskId + '\'' +
        ", taskStatus=" + taskStatus +
        ", revision='" + revision + '\'' +
        ", notificationId='" + notificationId + '\'' +
        ", createdAt=" + createdAt +
        ", destination=" + destination +
        ", lastResponse=" + lastResponse +
        '}';
  }
}
