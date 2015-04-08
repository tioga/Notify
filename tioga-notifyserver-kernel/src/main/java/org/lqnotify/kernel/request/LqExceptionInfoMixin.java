package org.lqnotify.kernel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lqnotify.notifier.request.LqExceptionInfo;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 11:36 PM
 */
public abstract class LqExceptionInfoMixin {
  @JsonCreator
  public LqExceptionInfoMixin(@JsonProperty("exceptionType") String exceptionType,
                              @JsonProperty("message") String message,
                              @JsonProperty("stackTrace") String stackTrace,
                              @JsonProperty("cause") LqExceptionInfo cause) {
  }
}

