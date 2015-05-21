package org.tiogasolutions.notify.kernel.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 11:01 PM
 */
public class CreateNotification {
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final ExceptionInfo exceptionInfo;
  private final Map<String, String> traitMap;

  public CreateNotification(@JsonProperty("topic") String topic,
                            @JsonProperty("summary") String summary,
                            @JsonProperty("trackingId") String trackingId,
                            @JsonProperty("createdAt") ZonedDateTime createdAt,
                            @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo,
                            @JsonProperty("traitMap") Map<String, String> traitMap) {
    this.topic = topic;
    this.summary = summary;
    this.trackingId = trackingId;
    this.createdAt = createdAt;
    this.exceptionInfo = exceptionInfo;
    Map<String, String> localMap = new HashMap<>();
    if (traitMap != null) {
      for(Map.Entry<String, String> trait : traitMap.entrySet()) {
        if (trait.getKey() != null) {
          localMap.put(trait.getKey().toLowerCase(), trait.getValue());
        }
      }
    }
    this.traitMap = localMap;

  }

  public String getTopic() {
    return topic;
  }

  public String getSummary() {
    return summary;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public ExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  public Map<String, String> getTraitMap() {
    return traitMap;
  }
}
