package org.tiogasolutions.notify.kernel.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
  private final Map<String, String> traitMap;
  private final List<Link> links;
  private final ExceptionInfo exceptionInfo;

  public CreateNotification(@JsonProperty("topic") String topic,
                            @JsonProperty("summary") String summary,
                            @JsonProperty("trackingId") String trackingId,
                            @JsonProperty("createdAt") ZonedDateTime createdAt,
                            @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo,
                            @JsonProperty("links") List<Link> links,
                            @JsonProperty("traitMap") Map<String, String> traitMap) {
    this.topic = topic;
    this.summary = summary;
    this.trackingId = trackingId;
    this.createdAt = (createdAt != null) ? createdAt : ZonedDateTime.now();
    this.exceptionInfo = exceptionInfo;
    this.links = (links != null) ? Collections.unmodifiableList(links) : Collections.emptyList();
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

  public List<Link> getLinks() {
    return links;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CreateNotification that = (CreateNotification) o;

    if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
    if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null) return false;
    if (links != null ? !links.equals(that.links) : that.links != null) return false;
    if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
    if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
    if (trackingId != null ? !trackingId.equals(that.trackingId) : that.trackingId != null) return false;
    if (traitMap != null ? !traitMap.equals(that.traitMap) : that.traitMap != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = topic != null ? topic.hashCode() : 0;
    result = 31 * result + (summary != null ? summary.hashCode() : 0);
    result = 31 * result + (trackingId != null ? trackingId.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    result = 31 * result + (traitMap != null ? traitMap.hashCode() : 0);
    result = 31 * result + (links != null ? links.hashCode() : 0);
    result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CreateNotification{" +
      "topic='" + topic + '\'' +
      ", summary='" + summary + '\'' +
      ", trackingId='" + trackingId + '\'' +
      ", createdAt=" + createdAt +
      ", traitMap=" + traitMap +
      ", links=" + links +
      ", exceptionInfo=" + exceptionInfo +
      '}';
  }
}
