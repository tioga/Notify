package org.tiogasolutions.notify.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Notification implements Comparable<Notification> {

  private final URI self;
  private final String domainName;
  private final String notificationId;
  private final String revision;
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final Map<String, String> traitMap;
  private final ExceptionInfo exceptionInfo;
  private final List<AttachmentInfo> attachmentInfoList;

  @JsonCreator
  public Notification(@JsonProperty("self") URI self,
                      @JsonProperty("domainName") String domainName,
                      @JsonProperty("notificationId") String notificationId,
                      @JsonProperty("revision") String revision,
                      @JsonProperty("topic") String topic,
                      @JsonProperty("summary") String summary,
                      @JsonProperty("trackingId") String trackingId,
                      @JsonProperty("createdAt") ZonedDateTime createdAt,
                      @JsonProperty("traitMap") Map<String, String> traitMap,
                      @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo,
                      @JsonProperty("attachmentInfoList") List<AttachmentInfo> attachmentInfoList) {
    this.self = self;
    this.domainName = domainName;
    this.notificationId = notificationId;
    this.revision = revision;
    this.topic = topic;
    this.summary = summary;
    this.trackingId = trackingId;
    this.createdAt = createdAt;
    this.traitMap = traitMap;
    this.exceptionInfo = exceptionInfo;
    this.attachmentInfoList = attachmentInfoList;
  }

  public NotificationRef toNotificationRef() {
    return new NotificationRef(domainName, notificationId, revision);
  }

  public URI getSelf() {
    return self;
  }

  public String getDomainName() {
    return domainName;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public String getRevision() {
    return revision;
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

  public LocalDateTime getCreatedAtLocal() {
    return createdAt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
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
  public int compareTo(Notification that) {
    int diff = this.createdAt.compareTo(that.createdAt);
    if (diff != 0) return diff;

    return this.notificationId.compareTo(that.notificationId);
  }

  @Override
  public String toString() {
    return "Notification{" +
        "self=" + self +
        ", domainName='" + domainName + '\'' +
        ", notificationId='" + notificationId + '\'' +
        ", revision='" + revision + '\'' +
        ", topic='" + topic + '\'' +
        ", summary='" + summary + '\'' +
        ", trackingId='" + trackingId + '\'' +
        ", createdAt=" + createdAt +
        ", traitMap=" + traitMap +
        ", exceptionInfo=" + exceptionInfo +
        ", attachmentInfoList=" + attachmentInfoList +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Notification that = (Notification) o;

    if (!domainName.equals(that.domainName)) return false;
    if (!notificationId.equals(that.notificationId)) return false;
    if (!revision.equals(that.revision)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = domainName.hashCode();
    result = 31 * result + notificationId.hashCode();
    result = 31 * result + revision.hashCode();
    return result;
  }
}
