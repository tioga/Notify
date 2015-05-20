package org.tiogasolutions.notify.kernel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.notifier.request.NotificationAttachment;
import org.tiogasolutions.notify.notifier.request.NotificationExceptionInfo;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;

public abstract class NotificationRequestMixin {
  @JsonCreator
  public NotificationRequestMixin(@JsonProperty("topic") String topic,
                                  @JsonProperty("summary") String summary,
                                  @JsonProperty("trackingId") String trackingId,
                                  @JsonProperty("createdAt") ZonedDateTime createdAt,
                                  @JsonProperty("traitsArg") Map<String, String> traitsArg,
                                  @JsonProperty("exceptionInfo") NotificationExceptionInfo exceptionInfo,
                                  @JsonProperty("attachmentsArg") Collection<NotificationAttachment> attachmentsArg) {
  }
}
