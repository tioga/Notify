package org.tiogasolutions.notify.pub.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExceptionInfo {

    private final String exceptionType;
    private final String message;
    private final String stackTrace;
    private final ExceptionInfo cause;

    @JsonCreator
    public ExceptionInfo(@JsonProperty("exceptionType") String exceptionType,
                         @JsonProperty("message") String message,
                         @JsonProperty("stackTrace") String stackTrace,
                         @JsonProperty("cause") ExceptionInfo cause) {

        this.exceptionType = exceptionType;
        this.message = message;
        this.stackTrace = (stackTrace == null) ? null : stackTrace.replace("\r", "");
        this.cause = cause;
    }

    public ExceptionInfo(Throwable throwable) {
        exceptionType = throwable.getClass().getName();
        this.message = ExceptionUtils.getMessage(throwable);
        this.stackTrace = ExceptionUtils.getStackTrace(throwable).replace("\r", "");

        if (throwable.getCause() == null || throwable == throwable.getCause()) {
            this.cause = null;
        } else {
            this.cause = new ExceptionInfo(throwable.getCause());
        }
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public ExceptionInfo getCause() {
        return cause;
    }

    @JsonIgnore
    public List<ExceptionInfo> getCauses() {
        List<ExceptionInfo> causes = new ArrayList<>();

        ExceptionInfo source = this.cause;
        while (source != null) {
            causes.add(source);
            source = source.getCause();
        }

        return Collections.unmodifiableList(causes);
    }

    public String getMessage() {
        return message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionInfo that = (ExceptionInfo) o;

        if (cause != null ? !cause.equals(that.cause) : that.cause != null) return false;
        if (exceptionType != null ? !exceptionType.equals(that.exceptionType) : that.exceptionType != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (stackTrace != null ? !stackTrace.equals(that.stackTrace) : that.stackTrace != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = exceptionType != null ? exceptionType.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (stackTrace != null ? stackTrace.hashCode() : 0);
        result = 31 * result + (cause != null ? cause.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExceptionInfo{" +
                "exceptionType='" + exceptionType + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
