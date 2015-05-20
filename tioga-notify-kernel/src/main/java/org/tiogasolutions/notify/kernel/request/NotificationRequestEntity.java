package org.tiogasolutions.notify.kernel.request;

import org.tiogasolutions.couchace.annotations.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.notify.notifier.NotifierException;
import org.tiogasolutions.notify.notifier.request.NotificationExceptionInfo;
import org.tiogasolutions.notify.notifier.request.NotificationRequest;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 7:15 PM
 */
@CouchEntity("NotificationRequest")
public class NotificationRequestEntity {

  private final String requestId;
  private String revision;
  public NotificationRequestEntityStatus requestStatus;
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final Map<String, String> traitMap;
  private final NotificationExceptionInfo exceptionInfo;
  private CouchAttachmentInfoMap attachmentInfoMap;

  @JsonCreator
  public NotificationRequestEntity(@JsonProperty("requestId") String requestId,
                                   @JsonProperty("revision") String revision,
                                   @JsonProperty("requestStatus") NotificationRequestEntityStatus requestStatus,
                                   @JsonProperty("topic") String topic,
                                   @JsonProperty("summary") String summary,
                                   @JsonProperty("trackingId") String trackingId,
                                   @JsonProperty("createdAt") ZonedDateTime createdAt,
                                   @JsonProperty("traitMap") Map<String, String> traitMap,
                                   @JsonProperty("exceptionInfo") NotificationExceptionInfo exceptionInfo) {

    this.requestId = requestId;
    this.revision = revision;
    this.requestStatus = requestStatus;
    this.topic = topic;
    this.summary = summary;
    this.trackingId = trackingId;
    this.createdAt = createdAt;
    this.exceptionInfo = exceptionInfo;
    this.traitMap = (traitMap != null) ? Collections.unmodifiableMap(new LinkedHashMap<>(traitMap)) : Collections.emptyMap();
  }

  public void ready() {
    if (requestStatus != NotificationRequestEntityStatus.SENDING) {
      throw new NotifierException("Cannot set request to ready, status is " + requestStatus);
    }
    requestStatus = NotificationRequestEntityStatus.READY;
  }

  public void processing() {
    if (requestStatus != NotificationRequestEntityStatus.READY) {
      throw new NotifierException("Cannot set request to processing, status is " + requestStatus);
    }
    requestStatus = NotificationRequestEntityStatus.PROCESSING;
  }

  public void completed() {
    if (requestStatus != NotificationRequestEntityStatus.PROCESSING) {
      throw new NotifierException("Cannot set request to completed, status is " + requestStatus);
    }
    requestStatus = NotificationRequestEntityStatus.COMPLETED;
  }

  public void failed() {
    if (requestStatus != NotificationRequestEntityStatus.PROCESSING) {
      throw new NotifierException("Cannot set request to failed, status is " + requestStatus);
    }
    requestStatus = NotificationRequestEntityStatus.FAILED;
  }

  public void ready(String currentRevision) {
    if (requestStatus != NotificationRequestEntityStatus.SENDING) {
      throw new NotifierException("Cannot change status to READY, current status is " + requestStatus);
    }
    requestStatus = NotificationRequestEntityStatus.READY;
    this.revision = currentRevision;
  }

  @CouchId
  public String getRequestId() {
    return requestId;
  }

  @CouchRevision
  public String getRevision() {
    return revision;
  }

  public void setRevision(String revision) {
    this.revision = revision;
  }

  public NotificationRequestEntityStatus getRequestStatus() {
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

  public NotificationExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  public List<AttachmentInfo> listAttachmentInfo() {
    List<AttachmentInfo> attachmentInfoList = new ArrayList<>();
    if (attachmentInfoMap != null) {
      for (Map.Entry<String, CouchAttachmentInfo> entry : attachmentInfoMap.entrySet()) {
        AttachmentInfo attachInfo = new AttachmentInfo(entry.getKey(), entry.getValue().getContentType());
        attachmentInfoList.add(attachInfo);
      }
    }
    return attachmentInfoList;
  }

  public NotificationRequest toRequest() {

    return new NotificationRequest(topic,
                         summary,
                         trackingId,
                         createdAt,
                         traitMap,
                         exceptionInfo,
                         Collections.emptyList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NotificationRequestEntity that = (NotificationRequestEntity) o;

    if (createdAt != null ? !createdAt.isEqual(that.createdAt) : that.createdAt != null) return false;
    if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null)
      return false;
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
    result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
    result = 31 * result + (traitMap != null ? traitMap.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "NotificationRequestEntity{" +
        "requestId='" + requestId + '\'' +
        ", revision='" + revision + '\'' +
        ", requestStatus=" + requestStatus +
        ", topic='" + topic + '\'' +
        ", summary='" + summary + '\'' +
        ", trackingId='" + trackingId + '\'' +
        ", createdAt=" + createdAt +
        ", exceptionInfo=" + exceptionInfo +
        ", traitMap=" + traitMap +
        '}';
  }

  public static NotificationRequestEntity newEntity(NotificationRequest request) {
    return new NotificationRequestEntity(
      TimeUuid.randomUUID().toString(),
      null,
      NotificationRequestEntityStatus.SENDING,
      request.getTopic(),
      request.getSummary(),
      request.getTrackingId(),
      request.getCreatedAt(),
      request.getTraitMap(),
      request.getExceptionInfo());
  }
}
