package org.tiogasolutions.notify.notifier.request;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: Harlan
 * Date: 1/24/2015
 * Time: 10:44 PM
 */
public final class NotificationRequest {

  public enum Status {
    SENDING, READY, PROCESSING, FAILED, COMPLETED
  }

  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final NotificationExceptionInfo exceptionInfo;
  private final Map<String, String> traitMap;
  private final List<NotificationAttachment> attachments;

  public NotificationRequest(String topic,
                             String summary,
                             String trackingId,
                             ZonedDateTime createdAt,
                             Map<String, String> traitsArg,
                             NotificationExceptionInfo exceptionInfo,
                             Collection<NotificationAttachment> attachmentsArg) {

    this.topic = (topic != null) ? topic : "none";
    this.summary = (summary != null) ? summary : "none";
    this.trackingId = trackingId;
    this.exceptionInfo = exceptionInfo;
    this.createdAt = (createdAt != null) ? createdAt : ZonedDateTime.now();
    Map<String, String> traitMap = new LinkedHashMap<>();
    if (traitsArg != null) {
      traitMap.putAll(traitsArg);
    }
    this.traitMap = Collections.unmodifiableMap(traitMap);
    List<NotificationAttachment> attachmentsList = new ArrayList<>();
    if (attachmentsArg != null) {
      attachmentsList.addAll(attachmentsArg);
    }
    this.attachments = Collections.unmodifiableList(attachmentsList);
  }

  public String getTopic() {
    return topic;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public String getSummary() {
    return summary;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public NotificationExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  public Map<String, String> getTraitMap() {
    return traitMap;
  }

  public List<NotificationAttachment> getAttachments() {
    return attachments;
  }
}
