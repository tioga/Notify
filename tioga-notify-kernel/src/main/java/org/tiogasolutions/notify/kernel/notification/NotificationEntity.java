package org.tiogasolutions.notify.kernel.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.couchace.annotations.*;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationRef;

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
    private final boolean internal;
    private final String notificationId;
    private final String domainName;
    private final String topic;
    private final String summary;
    private final String trackingId;
    private final ZonedDateTime createdAt;
    private final Map<String, String> traitMap;
    private final List<Link> links;
    private final ExceptionInfo exceptionInfo;
    private String revision;
    /**
     * Required for couch attachments -- looks unused but do not delete - HN
     */
    private CouchAttachmentInfoMap attachmentInfoMap;

    @JsonCreator
    public NotificationEntity(@JsonProperty(value = "internal", defaultValue = "false") boolean internal,
                              @JsonProperty("domainName") String domainName,
                              @JsonProperty("notificationId") String notificationId,
                              @JsonProperty("revision") String revision,
                              @JsonProperty("topic") String topic,
                              @JsonProperty("summary") String summary,
                              @JsonProperty("trackingId") String trackingId,
                              @JsonProperty("createdAt") ZonedDateTime createdAt,
                              @JsonProperty("traitMap") Map<String, String> traitMap,
                              @JsonProperty("links") List<Link> links,
                              @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo) {

        this.internal = internal;
        this.domainName = domainName;
        this.notificationId = notificationId;
        this.revision = revision;
        this.topic = topic;
        this.summary = summary;
        this.trackingId = trackingId;
        this.createdAt = (createdAt != null) ? createdAt : ZonedDateTime.now();
        this.links = (links != null) ? Collections.unmodifiableList(links) : null;
        this.exceptionInfo = exceptionInfo;
        this.traitMap = (traitMap != null) ? Collections.unmodifiableMap(traitMap) : Collections.emptyMap();
    }

    public static NotificationEntity newEntity(String domainName, CreateNotification create) {
        return new NotificationEntity(
                create.isInternal(),
                domainName,
                TimeUuid.randomUUID().toString(),
                null,
                create.getTopic(),
                create.getSummary(),
                create.getTrackingId(),
                ZonedDateTime.now(), // HACK - for now ignore createdAt in the create and just use local now.
                create.getTraitMap(),
                create.getLinks(),
                create.getExceptionInfo());
    }

    public NotificationRef toNotificationRef() {
        return new NotificationRef(domainName, notificationId, revision);
    }

    public Notification toNotification() {
        return new Notification(internal, null, domainName, notificationId, revision, topic, summary, trackingId, createdAt, traitMap, links, exceptionInfo, listAttachmentInfo());
    }

    public Notification toNotificationWithRevision(String revisionArg) {
        return new Notification(internal,null, domainName, notificationId, revisionArg, topic, summary, trackingId, createdAt, traitMap, links, exceptionInfo, listAttachmentInfo());
    }

    @CouchId
    public String getNotificationId() {
        return notificationId;
    }

    @CouchRevision
    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public boolean isInternal() {
        return internal;
    }

    public String getDomainName() {
        return domainName;
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

    public List<Link> getLinks() {
        return links;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationEntity that = (NotificationEntity) o;

        if (attachmentInfoMap != null ? !attachmentInfoMap.equals(that.attachmentInfoMap) : that.attachmentInfoMap != null)
            return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (domainName != null ? !domainName.equals(that.domainName) : that.domainName != null) return false;
        if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null) return false;
        if (links != null ? !links.equals(that.links) : that.links != null) return false;
        if (notificationId != null ? !notificationId.equals(that.notificationId) : that.notificationId != null)
            return false;
        if (revision != null ? !revision.equals(that.revision) : that.revision != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        if (trackingId != null ? !trackingId.equals(that.trackingId) : that.trackingId != null) return false;
        if (traitMap != null ? !traitMap.equals(that.traitMap) : that.traitMap != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = notificationId != null ? notificationId.hashCode() : 0;
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (domainName != null ? domainName.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (trackingId != null ? trackingId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (traitMap != null ? traitMap.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
        result = 31 * result + (attachmentInfoMap != null ? attachmentInfoMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "notificationId='" + notificationId + '\'' +
                ", revision='" + revision + '\'' +
                ", domainName='" + domainName + '\'' +
                ", topic='" + topic + '\'' +
                ", summary='" + summary + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", createdAt=" + createdAt +
                ", traitMap=" + traitMap +
                ", links=" + links +
                ", exceptionInfo=" + exceptionInfo +
                ", attachmentInfoMap=" + attachmentInfoMap +
                '}';
    }
}
