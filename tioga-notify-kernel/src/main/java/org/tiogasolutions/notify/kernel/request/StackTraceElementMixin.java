package org.tiogasolutions.notify.kernel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 11:36 PM
 */
public abstract class StackTraceElementMixin {
  @JsonCreator
  public StackTraceElementMixin(@JsonProperty("className") String className,
                                @JsonProperty("methodName") String methodName,
                                @JsonProperty("fileName") String fileName,
                                @JsonProperty("lineNumber") int lineNumber) {
  }
}

