package org.tiogasolutions.notify.kernel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.notifier.request.NotificationExceptionInfo;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 11:36 PM
 */
public abstract class ExceptionInfoMixin {
  @JsonCreator
  public ExceptionInfoMixin(@JsonProperty("exceptionType") String exceptionType,
                            @JsonProperty("message") String message,
                            @JsonProperty("stackTrace") String stackTrace,
                            @JsonProperty("cause") NotificationExceptionInfo cause) {
  }
}

