package org.tiogasolutions.notify.notifier.send;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: Harlan
 * Date: 1/24/2015
 * Time: 10:44 PM
 */
public final class SendNotificationRequest {

    public enum Status {
        SENDING, READY, PROCESSING, FAILED, COMPLETED
    }

    private final boolean internal;
    private final String topic;
    private final String summary;
    private final String trackingId;
    private final ZonedDateTime createdAt;
    private final Map<String, String> traitMap;
    private final List<NotificationLink> links;
    private final NotificationExceptionInfo exceptionInfo;
    private final List<NotificationAttachment> attachments;
    public SendNotificationRequest(boolean internal,
                                   String topic,
                                   String summary,
                                   String trackingId,
                                   ZonedDateTime createdAt,
                                   Map<String, String> traitsArg,
                                   List<NotificationLink> linksArg,
                                   NotificationExceptionInfo exceptionInfo,
                                   Collection<NotificationAttachment> attachmentsArg) {

        this.internal = internal;
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

        List<NotificationLink> linksList = new ArrayList<>();
        if (linksArg != null) {
            linksList.addAll(linksArg);
        }
        this.links = Collections.unmodifiableList(linksList);

        List<NotificationAttachment> attachmentsList = new ArrayList<>();
        if (attachmentsArg != null) {
            attachmentsList.addAll(attachmentsArg);
        }
        this.attachments = Collections.unmodifiableList(attachmentsList);
    }

    public boolean isInternal() {
        return internal;
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

    public List<NotificationLink> getLinks() {
        return links;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SendNotificationRequest that = (SendNotificationRequest) o;

        if (internal != that.internal) return false;
        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (trackingId != null ? !trackingId.equals(that.trackingId) : that.trackingId != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (traitMap != null ? !traitMap.equals(that.traitMap) : that.traitMap != null) return false;
        if (links != null ? !links.equals(that.links) : that.links != null) return false;
        if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null) return false;
        return attachments != null ? attachments.equals(that.attachments) : that.attachments == null;
    }

    @Override
    public int hashCode() {
        int result = (internal ? 1 : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (trackingId != null ? trackingId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (traitMap != null ? traitMap.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SendNotificationRequest{" +
                "internal=" + internal +
                ", topic='" + topic + '\'' +
                ", summary='" + summary + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", createdAt=" + createdAt +
                ", traitMap=" + traitMap +
                ", links=" + links +
                ", exceptionInfo=" + exceptionInfo +
                ", attachments=" + attachments +
                '}';
    }
}
