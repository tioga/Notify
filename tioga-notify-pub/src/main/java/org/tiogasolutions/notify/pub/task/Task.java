package org.tiogasolutions.notify.pub.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.pub.route.Destination;

import java.net.URI;
import java.time.ZonedDateTime;

public class Task {

  private final URI self;
  private final String taskId;
  private final String revision;
  private final TaskStatus taskStatus;
  private final String notificationId;
  private final ZonedDateTime createdAt;
  private final Destination destination;
  private final TaskResponse taskResponse;

  @JsonCreator
  public Task(@JsonProperty("self") URI self,
              @JsonProperty("taskId") String taskId,
              @JsonProperty("revision") String revision,
              @JsonProperty("taskStatus") TaskStatus taskStatus,
              @JsonProperty("notificationId") String notificationId,
              @JsonProperty("createdAt") ZonedDateTime createdAt,
              @JsonProperty("destination") Destination destination,
              @JsonProperty("lastResponse") TaskResponse taskResponse) {

    this.self = self;
    this.taskId = taskId;
    this.revision = revision;
    this.notificationId = notificationId;
    this.createdAt = createdAt;
    this.taskStatus = taskStatus;
    this.destination = destination;
    this.taskResponse = taskResponse;
  }

  public String getTaskId() {
    return taskId;
  }

  public URI getSelf() {
    return self;
  }

  public TaskStatus getTaskStatus() {
    return taskStatus;
  }

  public String getRevision() {
    return revision;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public Destination getDestination() {
    return destination;
  }

  public TaskResponse getTaskResponse() {
    return taskResponse;
  }

  public TaskRef toTaskRef() {
    return new TaskRef(taskId, revision, notificationId);
  }

//  @JsonIgnore
//  public String getLabel() {
//    List<String> argValues = new ArrayList<>();
//    for (ArgValue value : destination.getArgValueMap().getArgMap().values()) {
//      argValues.add(value.asString());
//    }
//    return String.format("Task %s: %s %s", taskId, destination.getProvider(), argValues);
//  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Task task = (Task) o;

    if (createdAt != null ? !createdAt.equals(task.createdAt) : task.createdAt != null) return false;
    if (destination != null ? !destination.equals(task.destination) : task.destination != null) return false;
    if (notificationId != null ? !notificationId.equals(task.notificationId) : task.notificationId != null)
      return false;
    if (revision != null ? !revision.equals(task.revision) : task.revision != null) return false;
    if (self != null ? !self.equals(task.self) : task.self != null) return false;
    if (taskId != null ? !taskId.equals(task.taskId) : task.taskId != null) return false;
    if (taskResponse != null ? !taskResponse.equals(task.taskResponse) : task.taskResponse != null) return false;
    if (taskStatus != task.taskStatus) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = self != null ? self.hashCode() : 0;
    result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
    result = 31 * result + (revision != null ? revision.hashCode() : 0);
    result = 31 * result + (taskStatus != null ? taskStatus.hashCode() : 0);
    result = 31 * result + (notificationId != null ? notificationId.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    result = 31 * result + (destination != null ? destination.hashCode() : 0);
    result = 31 * result + (taskResponse != null ? taskResponse.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Task{" +
        "self=" + self +
        ", taskId='" + taskId + '\'' +
        ", revision='" + revision + '\'' +
        ", taskStatus=" + taskStatus +
        ", notificationId='" + notificationId + '\'' +
        ", createdAt=" + createdAt +
        ", destination=" + destination +
        ", taskResponse=" + taskResponse +
        '}';
  }
}