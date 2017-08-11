package org.tiogasolutions.notify.kernel.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.couchace.annotations.*;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;

import javax.ws.rs.BadRequestException;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 7:15 PM
 */
@CouchEntity(NotificationRequestEntity.ENTITY_TYPE)
public class NotificationRequestEntity {

    public static final String ENTITY_TYPE = "NotificationRequest";
    private final String requestId;
    private final boolean internal;
    private final String topic;
    private final String summary;
    private final String trackingId;
    private final ZonedDateTime createdAt;
    private final Map<String, String> traitMap;
    private final List<Link> links;
    private final ExceptionInfo exceptionInfo;
    public NotificationRequestStatus requestStatus;
    private String revision;
    private CouchAttachmentInfoMap attachmentInfoMap;

    @JsonCreator
    public NotificationRequestEntity(@JsonProperty(value="internal", defaultValue="false") boolean internal,
                                     @JsonProperty("requestId") String requestId,
                                     @JsonProperty("revision") String revision,
                                     @JsonProperty("requestStatus") NotificationRequestStatus requestStatus,
                                     @JsonProperty("topic") String topic,
                                     @JsonProperty("summary") String summary,
                                     @JsonProperty("trackingId") String trackingId,
                                     @JsonProperty("createdAt") ZonedDateTime createdAt,
                                     @JsonProperty("traitMap") Map<String, String> traitMap,
                                     @JsonProperty("links") List<Link> links,
                                     @JsonProperty("exceptionInfo") ExceptionInfo exceptionInfo) {
        this.internal = internal;
        this.requestId = requestId;
        this.revision = revision;
        this.requestStatus = requestStatus;
        this.topic = topic;
        this.summary = summary;
        this.trackingId = trackingId;
        this.createdAt = createdAt;
        this.links = (links != null) ? Collections.unmodifiableList(links) : Collections.emptyList();
        this.exceptionInfo = exceptionInfo;
        this.traitMap = (traitMap != null) ? Collections.unmodifiableMap(new LinkedHashMap<>(traitMap)) : Collections.emptyMap();
    }

    public static NotificationRequestEntity newEntity(NotificationRequest request) {

        String requestId = request.getRequestId();
        if (requestId == null) {
            requestId = TimeUuid.randomUUID().toString();
        }

        NotificationRequestStatus requestStatus = request.getRequestStatus();
        if (requestStatus == null || requestStatus.isNotReady()) {
            requestStatus = NotificationRequestStatus.SENDING;
        }

        ZonedDateTime createdAt = request.getCreatedAt();
        if (createdAt == null) {
            ZonedDateTime.now();
        }

        return new NotificationRequestEntity(
                request.isInternal(),
                requestId,
                null,
                requestStatus,
                request.getTopic(),
                request.getSummary(),
                request.getTrackingId(),
                createdAt,
                request.getTraitMap(),
                request.getLinks(),
                request.getExceptionInfo());
    }

    public void ready() {
        if (requestStatus != NotificationRequestStatus.SENDING) {
            throw new BadRequestException("Cannot set request to ready, status is " + requestStatus);
        }
        requestStatus = NotificationRequestStatus.READY;
    }

    public void processing() {
        if (requestStatus != NotificationRequestStatus.READY) {
            throw new BadRequestException("Cannot set request to processing, status is " + requestStatus);
        }
        requestStatus = NotificationRequestStatus.PROCESSING;
    }

    public void completed() {
        if (requestStatus != NotificationRequestStatus.PROCESSING) {
            throw new BadRequestException("Cannot set request to completed, status is " + requestStatus);
        }
        requestStatus = NotificationRequestStatus.COMPLETED;
    }

    public void failed() {
        if (requestStatus != NotificationRequestStatus.PROCESSING) {
            throw new BadRequestException("Cannot set request to failed, status is " + requestStatus);
        }
        requestStatus = NotificationRequestStatus.FAILED;
    }

    public void ready(String currentRevision) {
        if (requestStatus != NotificationRequestStatus.SENDING) {
            throw new BadRequestException("Cannot change status to READY, current status is " + requestStatus);
        }
        requestStatus = NotificationRequestStatus.READY;
        this.revision = currentRevision;
    }

    public NotificationRequest toRequest() {
        return new NotificationRequest(
                isInternal(),
                getRequestId(),
                getRevision(),
                getRequestStatus(),
                getTopic(),
                getSummary(),
                getTrackingId(),
                getCreatedAt(),
                getTraitMap(),
                getLinks(),
                getExceptionInfo(),
                listAttachmentInfo()
        );
    }


    @CouchId
    public String getRequestId() {
        return requestId;
    }

    @CouchRevision
    public String getRevision() {
        return revision;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setRevision(String revision) {
        this.revision = revision;
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

        NotificationRequestEntity that = (NotificationRequestEntity) o;

        if (internal != that.internal) return false;
        if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null) return false;
        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (trackingId != null ? !trackingId.equals(that.trackingId) : that.trackingId != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (traitMap != null ? !traitMap.equals(that.traitMap) : that.traitMap != null) return false;
        if (links != null ? !links.equals(that.links) : that.links != null) return false;
        if (exceptionInfo != null ? !exceptionInfo.equals(that.exceptionInfo) : that.exceptionInfo != null) return false;
        if (requestStatus != that.requestStatus) return false;
        if (revision != null ? !revision.equals(that.revision) : that.revision != null) return false;
        return attachmentInfoMap != null ? attachmentInfoMap.equals(that.attachmentInfoMap) : that.attachmentInfoMap == null;
    }

    @Override
    public int hashCode() {
        int result = requestId != null ? requestId.hashCode() : 0;
        result = 31 * result + (internal ? 1 : 0);
        result = 31 * result + (topic != null ? topic.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (trackingId != null ? trackingId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (traitMap != null ? traitMap.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        result = 31 * result + (exceptionInfo != null ? exceptionInfo.hashCode() : 0);
        result = 31 * result + (requestStatus != null ? requestStatus.hashCode() : 0);
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (attachmentInfoMap != null ? attachmentInfoMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NotificationRequestEntity{" +
                "requestId='" + requestId + '\'' +
                ", internal=" + internal +
                ", topic='" + topic + '\'' +
                ", summary='" + summary + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", createdAt=" + createdAt +
                ", traitMap=" + traitMap +
                ", links=" + links +
                ", exceptionInfo=" + exceptionInfo +
                ", requestStatus=" + requestStatus +
                ", revision='" + revision + '\'' +
                ", attachmentInfoMap=" + attachmentInfoMap +
                '}';
    }
}
