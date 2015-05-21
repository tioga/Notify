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
  private String revision;
  public NotificationRequestStatus requestStatus;
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final Map<String, String> traitMap;
  private final ExceptionInfo exceptionInfo;
  private final List<AttachmentInfo> attachmentInfos;

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
                             @JsonProperty("attachmentInfos") List<AttachmentInfo> attachmentInfos) {

    this.requestId = requestId;
    this.revision = revision;
    this.requestStatus = requestStatus;
    this.topic = topic;
    this.summary = summary;
    this.trackingId = trackingId;
    this.createdAt = createdAt;
    this.exceptionInfo = exceptionInfo;
    this.traitMap = (traitMap != null) ? Collections.unmodifiableMap(traitMap) : Collections.emptyMap();
    this.attachmentInfos = (attachmentInfos != null) ? Collections.unmodifiableList(attachmentInfos) : Collections.emptyList();
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

  public List<AttachmentInfo> getAttachmentInfos() {
    return attachmentInfos;
  }
}
