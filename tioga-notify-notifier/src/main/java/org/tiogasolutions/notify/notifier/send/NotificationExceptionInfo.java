package org.tiogasolutions.notify.notifier.send;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * User: Harlan
 * Date: 1/24/2015
 * Time: 11:08 PM
 */
public final class NotificationExceptionInfo {

  private final String exceptionType;
  private final String message;
  private final String stackTrace;
  private final NotificationExceptionInfo cause;

  public static String getStackTrace(Throwable e) {
    StringWriter writer = new StringWriter();
    PrintWriter pw = new PrintWriter(writer);
    e.printStackTrace(pw);
    return writer.toString().replace("\r", "");
  }

  public NotificationExceptionInfo(Throwable t) {
    if (t != null) {
      exceptionType = t.getClass().getName();
      message = t.getMessage();
      stackTrace = getStackTrace(t);
      cause = (t.getCause() != null) ? new NotificationExceptionInfo(t.getCause()) : null;
    } else {
      exceptionType = "undefined";
      message = "none";
      stackTrace = "none";
      cause = null;
    }
  }

  public NotificationExceptionInfo(String exceptionType,
                                   String message,
                                   String stackTrace,
                                   NotificationExceptionInfo cause) {
    this.exceptionType = (exceptionType != null) ? exceptionType : "undefined";
    this.message = (message != null) ? message : "none";
    this.stackTrace = (stackTrace != null) ? stackTrace : "none";
    this.cause = cause;
  }

  public String getExceptionType() {
    return exceptionType;
  }

  public String getMessage() {
    return message;
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public NotificationExceptionInfo getCause() {
    return cause;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NotificationExceptionInfo that = (NotificationExceptionInfo) o;

    if (cause != null ? !cause.equals(that.cause) : that.cause != null) return false;
    if (!exceptionType.equals(that.exceptionType)) return false;
    if (!message.equals(that.message)) return false;
    if (!stackTrace.equals(that.stackTrace)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = exceptionType.hashCode();
    result = 31 * result + message.hashCode();
    result = 31 * result + stackTrace.hashCode();
    result = 31 * result + (cause != null ? cause.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ExceptionInfo{" +
        "exceptionType='" + exceptionType + '\'' +
        ", message='" + message + '\'' +
        ", stackTrace=" + stackTrace +
        ", cause=" + cause +
        '}';
  }
}
