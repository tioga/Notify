package org.tiogasolutions.notify.pub.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 7:15 PM
 */
public class NotificationRequest {
  private final String requestId;
  private final String revision;
  private final  NotificationRequestStatus requestStatus;
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final Map<String, String> traitMap;
  private final ExceptionInfo exceptionInfo;
  private final List<AttachmentInfo> attachmentInfoList;

  @JsonCreator
  public NotificationRequest(@JsonProperty("requestId") String requestId,
                             @JsonProperty("revision") String revision,
                             @JsonProperty("requestStatus") NotificationRequestStatus requestStatus,
                             @JsonProperty("topic") String topic,
                             @JsonProperty("summary") String summary,
                             @JsonProperty("trackingId") String trackingId,
                             @JsonProperty("createdAt") ZonedDateTime createdAt,
                             @JsonProperty("traitMap") Map<String, String> traitMap,
                             @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo,
                             @JsonProperty("attachmentInfoList") List<AttachmentInfo> attachmentInfoList) {

    this.requestId = requestId;
    this.revision = revision;
    this.requestStatus = requestStatus;
    this.topic = topic;
    this.summary = summary;
    this.trackingId = trackingId;
    this.createdAt = createdAt;
    this.exceptionInfo = exceptionInfo;
    this.traitMap = (traitMap != null) ? Collections.unmodifiableMap(traitMap) : Collections.emptyMap();
    this.attachmentInfoList = (attachmentInfoList != null) ? Collections.unmodifiableList(attachmentInfoList) : Collections.emptyList();
  }

  public String getRequestId() {
    return requestId;
  }

  public String getRevision() {
    return revision;
  }

  public NotificationRequestStatus getRequestStatus() {
    return requestStatus;
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

  public Map<String, String> getTraitMap() {
    return traitMap;
  }

  public ExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  public List<AttachmentInfo> getAttachmentInfoList() {
    return attachmentInfoList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NotificationRequest that = (NotificationRequest) o;

    if (attachmentInfoList != null ? !attachmentInfoList.equals(that.attachmentInfoList) : that.attachmentInfoList != null)
      return false;
    if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
    if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null) return false;
    if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null) return false;
    if (requestStatus != that.requestStatus) return false;
    if (revision != null ? !revision.equals(that.revision) : that.revision != null) return false;
    if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
    if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
    if (trackingId != null ? !trackingId.equals(that.trackingId) : that.trackingId != null) return false;
    if (traitMap != null ? !traitMap.equals(that.traitMap) : that.traitMap != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = requestId != null ? requestId.hashCode() : 0;
    result = 31 * result + (revision != null ? revision.hashCode() : 0);
    result = 31 * result + (requestStatus != null ? requestStatus.hashCode() : 0);
    result = 31 * result + (topic != null ? topic.hashCode() : 0);
    result = 31 * result + (summary != null ? summary.hashCode() : 0);
    result = 31 * result + (trackingId != null ? trackingId.hashCode() : 0);
    result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
    result = 31 * result + (traitMap != null ? traitMap.hashCode() : 0);
    result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
    result = 31 * result + (attachmentInfoList != null ? attachmentInfoList.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "NotificationRequest{" +
        "requestId='" + requestId + '\'' +
        ", revision='" + revision + '\'' +
        ", requestStatus=" + requestStatus +
        ", topic='" + topic + '\'' +
        ", summary='" + summary + '\'' +
        ", trackingId='" + trackingId + '\'' +
        ", createdAt=" + createdAt +
        ", traitMap=" + traitMap +
        ", exceptionInfo=" + exceptionInfo +
        ", attachmentInfoList=" + attachmentInfoList +
        '}';
  }
}
