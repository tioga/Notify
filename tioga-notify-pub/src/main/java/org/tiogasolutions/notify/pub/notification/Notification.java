package org.tiogasolutions.notify.pub.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    private final List<Link> links;
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
                        @JsonProperty("links") List<Link> links,
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

        this.exceptionInfo = exceptionInfo;

        this.traitMap = (traitMap == null) ?
                Collections.emptyMap() :
                Collections.unmodifiableSortedMap(new TreeMap<>(traitMap));

        this.links = (links == null) ?
                Collections.emptyList() :
                Collections.unmodifiableList(links);

        this.attachmentInfoList = (attachmentInfoList == null) ?
                Collections.emptyList() :
                Collections.unmodifiableList(attachmentInfoList);
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

    public boolean hasTrait(String name, String expected) {
        if (name != null && traitMap.containsKey(name.toLowerCase())) {
            String value = traitMap.get(name);
            if (value == null) {
                return expected == null;
            } else {
                return value.toLowerCase().equals(expected.toLowerCase());
            }
        }
        return false;
    }

    public boolean hasTrait(String name) {
        return (name != null && traitMap.containsKey(name.toLowerCase()));
    }

    public Map<String, String> getTraitMap() {
        return traitMap;
    }

    public List<Link> getLinks() {
        return links;
    }

    public boolean hasException() {
        return exceptionInfo != null;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (attachmentInfoList != null ? !attachmentInfoList.equals(that.attachmentInfoList) : that.attachmentInfoList != null)
            return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (domainName != null ? !domainName.equals(that.domainName) : that.domainName != null) return false;
        if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null) return false;
        if (links != null ? !links.equals(that.links) : that.links != null) return false;
        if (notificationId != null ? !notificationId.equals(that.notificationId) : that.notificationId != null)
            return false;
        if (revision != null ? !revision.equals(that.revision) : that.revision != null) return false;
        if (self != null ? !self.equals(that.self) : that.self != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        if (trackingId != null ? !trackingId.equals(that.trackingId) : that.trackingId != null) return false;
        if (traitMap != null ? !traitMap.equals(that.traitMap) : that.traitMap != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = self != null ? self.hashCode() : 0;
        result = 31 * result + (domainName != null ? domainName.hashCode() : 0);
        result = 31 * result + (notificationId != null ? notificationId.hashCode() : 0);
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (trackingId != null ? trackingId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (traitMap != null ? traitMap.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
        result = 31 * result + (attachmentInfoList != null ? attachmentInfoList.hashCode() : 0);
        return result;
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
                ", links=" + links +
                ", exceptionInfo=" + exceptionInfo +
                ", attachmentInfoList=" + attachmentInfoList +
                '}';
    }
}
