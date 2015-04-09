package org.tiogasolutions.notifyserver.kernel.request;

import org.tiogasolutions.couchace.annotations.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.notifyserver.notifier.LqException;
import org.tiogasolutions.notifyserver.notifier.request.LqExceptionInfo;
import org.tiogasolutions.notifyserver.notifier.request.LqRequest;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 7:15 PM
 */
@CouchEntity("LqRequest")
public class LqRequestEntity {

  private final String requestId;
  private String revision;
  public LqRequestEntityStatus requestStatus;
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final Map<String, String> traitMap;
  private final LqExceptionInfo exceptionInfo;
  private CouchAttachmentInfoMap attachmentInfoMap;

  @JsonCreator
  public LqRequestEntity(@JsonProperty("requestId") String requestId,
                         @JsonProperty("revision") String revision,
                         @JsonProperty("requestStatus") LqRequestEntityStatus requestStatus,
                         @JsonProperty("topic") String topic,
                         @JsonProperty("summary") String summary,
                         @JsonProperty("trackingId") String trackingId,
                         @JsonProperty("createdAt") ZonedDateTime createdAt,
                         @JsonProperty("traitMap") Map<String, String> traitMap,
                         @JsonProperty("exceptionInfo") LqExceptionInfo exceptionInfo) {

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
    if (requestStatus != LqRequestEntityStatus.SENDING) {
      throw new LqException("Cannot set request to ready, status is " + requestStatus);
    }
    requestStatus = LqRequestEntityStatus.READY;
  }

  public void processing() {
    if (requestStatus != LqRequestEntityStatus.READY) {
      throw new LqException("Cannot set request to processing, status is " + requestStatus);
    }
    requestStatus = LqRequestEntityStatus.PROCESSING;
  }

  public void completed() {
    if (requestStatus != LqRequestEntityStatus.PROCESSING) {
      throw new LqException("Cannot set request to completed, status is " + requestStatus);
    }
    requestStatus = LqRequestEntityStatus.COMPLETED;
  }

  public void failed() {
    if (requestStatus != LqRequestEntityStatus.PROCESSING) {
      throw new LqException("Cannot set request to failed, status is " + requestStatus);
    }
    requestStatus = LqRequestEntityStatus.FAILED;
  }

  public void ready(String currentRevision) {
    if (requestStatus != LqRequestEntityStatus.SENDING) {
      throw new LqException("Cannot change status to READY, current status is " + requestStatus);
    }
    requestStatus = LqRequestEntityStatus.READY;
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

  public LqRequestEntityStatus getRequestStatus() {
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

  public LqExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  public List<LqAttachmentInfo> listAttachmentInfo() {
    List<LqAttachmentInfo> attachmentInfoList = new ArrayList<>();
    if (attachmentInfoMap != null) {
      for (Map.Entry<String, CouchAttachmentInfo> entry : attachmentInfoMap.entrySet()) {
        LqAttachmentInfo attachInfo = new LqAttachmentInfo(entry.getKey(), entry.getValue().getContentType());
        attachmentInfoList.add(attachInfo);
      }
    }
    return attachmentInfoList;
  }

  public LqRequest toRequest() {

    return new LqRequest(topic,
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

    LqRequestEntity that = (LqRequestEntity) o;

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
    return "LqRequestEntity{" +
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

  public static LqRequestEntity newEntity(LqRequest request) {
    return new LqRequestEntity(
      TimeUuid.randomUUID().toString(),
      null,
      LqRequestEntityStatus.SENDING,
      request.getTopic(),
      request.getSummary(),
      request.getTrackingId(),
      request.getCreatedAt(),
      request.getTraitMap(),
      request.getExceptionInfo());
  }
}
