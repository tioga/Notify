package org.tiogasolutions.notify.pub.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ApiBadRequestException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;

/**
 * Created by harlan on 3/7/15.
 */
public class TaskResponse {
  private final TaskResponseAction responseAction;
  private final String message;
  private final ExceptionInfo exceptionInfo;

  public static TaskResponse retry(String message) {
    return new TaskResponse(TaskResponseAction.RETRY, message, null);
  }

  public static TaskResponse complete(String message) {
    return new TaskResponse(TaskResponseAction.COMPLETE, message, null);
  }

  public static TaskResponse fail(String message, Throwable ex) {
    return new TaskResponse(TaskResponseAction.FAIL, message, new ExceptionInfo(ex));
  }

  public static TaskResponse fail(String message) {
    return new TaskResponse(TaskResponseAction.FAIL, message, null);
  }

  @JsonCreator
  private TaskResponse(@JsonProperty("responseAction") TaskResponseAction responseAction,
                       @JsonProperty("message") String message,
                       @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo) {

    this.responseAction = ExceptionUtils.assertNotNull(responseAction, "responseAction", ApiBadRequestException.class);
    this.message = message;
    this.exceptionInfo = exceptionInfo;
  }

  public TaskResponseAction getResponseAction() {
    return responseAction;
  }

  public String getMessage() {
    return message;
  }

  public ExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TaskResponse that = (TaskResponse) o;

    if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null) return false;
    if (message != null ? !message.equals(that.message) : that.message != null) return false;
    if (responseAction != that.responseAction) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = responseAction != null ? responseAction.hashCode() : 0;
    result = 31 * result + (message != null ? message.hashCode() : 0);
    result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TaskResponse{" +
        "responseAction=" + responseAction +
        ", message='" + message + '\'' +
        ", exceptionInfo=" + exceptionInfo +
        '}';
  }
}
