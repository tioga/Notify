package org.tiogasolutions.notify.kernel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.notifier.request.LqAttachment;
import org.tiogasolutions.notify.notifier.request.LqExceptionInfo;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;

public abstract class LqRequestMixin {
  @JsonCreator
  public LqRequestMixin(@JsonProperty("topic") String topic,
                        @JsonProperty("summary") String summary,
                        @JsonProperty("trackingId") String trackingId,
                        @JsonProperty("createdAt") ZonedDateTime createdAt,
                        @JsonProperty("traitsArg") Map<String, String> traitsArg,
                        @JsonProperty("exceptionInfo") LqExceptionInfo exceptionInfo,
                        @JsonProperty("attachmentsArg") Collection<LqAttachment> attachmentsArg) {
  }
}
