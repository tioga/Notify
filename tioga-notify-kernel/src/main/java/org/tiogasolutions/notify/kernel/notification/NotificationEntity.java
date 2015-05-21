package org.tiogasolutions.notify.kernel.notification;

import org.tiogasolutions.couchace.annotations.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.notify.pub.AttachmentInfo;
import org.tiogasolutions.notify.pub.Notification;
import org.tiogasolutions.notify.pub.ExceptionInfo;
import org.tiogasolutions.notify.pub.NotificationRef;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 7:15 PM
 */
@CouchEntity("Notification")
public class NotificationEntity {
  private final String notificationId;
  private String revision;
  private final String domainName;
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final Map<String, String> traitMap;
  // TODO should we use NotificationExceptionInfo instead of pub
  private final ExceptionInfo exceptionInfo;
  /**
   * Required for couch attachments -- looks unused but do not delete - HN
   */
  private CouchAttachmentInfoMap attachmentInfoMap;

  public static NotificationEntity newEntity(String domainName, CreateNotification create) {
    return new NotificationEntity(
        domainName,
        TimeUuid.randomUUID().toString(),
        null,
        create.getTopic(),
        create.getSummary(),
        create.getTrackingId(),
        create.getCreatedAt(),
        create.getTraitMap(),
        create.getExceptionInfo());
  }

  @JsonCreator
  public NotificationEntity(@JsonProperty("domainName") String domainName,
                            @JsonProperty("notificationId") String notificationId,
                            @JsonProperty("revision") String revision,
                            @JsonProperty("topic") String topic,
                            @JsonProperty("summary") String summary,
                            @JsonProperty("trackingId") String trackingId,
                            @JsonProperty("createdAt") ZonedDateTime createdAt,
                            @JsonProperty("traitMap") Map<String, String> traitMap,
                            @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo) {

    this.domainName = domainName;
    this.notificationId = notificationId;
    this.revision = revision;
    this.topic = topic;
    this.summary = summary;
    this.trackingId = trackingId;
    this.createdAt = createdAt;
    this.exceptionInfo = exceptionInfo;
    this.traitMap = (traitMap != null) ? Collections.unmodifiableMap(traitMap) : Collections.emptyMap();
  }

  public NotificationRef toNotificationRef() {
    return new NotificationRef(domainName, notificationId, revision);
  }

  public Notification toNotification() {
    return new Notification(null, domainName, notificationId, revision, topic, summary, trackingId, createdAt, traitMap, exceptionInfo, listAttachmentInfo());
  }

  public Notification toNotificationWithRevision(String revisionArg) {
    return new Notification(null, domainName, notificationId, revisionArg, topic, summary, trackingId, createdAt, traitMap, exceptionInfo, listAttachmentInfo());
  }

  @CouchId
  public String getNotificationId() {
    return notificationId;
  }

  @CouchRevision
  public String getRevision() {
    return revision;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setRevision(String revision) {
    this.revision = revision;
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
}
