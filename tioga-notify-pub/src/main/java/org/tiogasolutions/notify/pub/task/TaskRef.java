package org.tiogasolutions.notify.pub.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskRef {

    private final String taskId;
    private final String revision;
    private final String notificationId;

    @JsonCreator
    public TaskRef(@JsonProperty("taskId") String taskId,
                   @JsonProperty("revision") String revision,
                   @JsonProperty("notificationId") String notificationId) {

        this.taskId = taskId;
        this.revision = revision;
        this.notificationId = notificationId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getRevision() {
        return revision;
    }

    public String getNotificationId() {
        return notificationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskRef taskRef = (TaskRef) o;

        if (notificationId != null ? !notificationId.equals(taskRef.notificationId) : taskRef.notificationId != null)
            return false;
        if (revision != null ? !revision.equals(taskRef.revision) : taskRef.revision != null) return false;
        if (taskId != null ? !taskId.equals(taskRef.taskId) : taskRef.taskId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = taskId != null ? taskId.hashCode() : 0;
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (notificationId != null ? notificationId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskRef{" +
                "taskId='" + taskId + '\'' +
                ", revision='" + revision + '\'' +
                ", notificationId='" + notificationId + '\'' +
                '}';
    }
}