package org.tiogasolutions.notify.kernel.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.http.CouchMediaType;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.couchace.core.api.response.CouchResponse;
import org.tiogasolutions.couchace.core.api.response.GetAttachmentResponse;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.notify.kernel.common.CouchConst;
import org.tiogasolutions.notify.pub.attachment.AttachmentHolder;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;

import java.util.List;

import static java.lang.String.format;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 4:53 PM
 */
public class NotificationRequestStore {

    private final CouchDatabase couchDatabase;

    @Autowired
    public NotificationRequestStore(CouchDatabase couchDatabase) {
        this.couchDatabase = couchDatabase;
    }

    public CouchDatabase getCouchDatabase() {
        return couchDatabase;
    }

    public WriteResponse save(NotificationRequestEntity notificationRequestEntity) {
        return couchDatabase.put()
                .entity(notificationRequestEntity)
                .execute();
    }

    public NotificationRequestEntity saveAndReload(NotificationRequestEntity notificationRequestEntity) {
        couchDatabase.put()
                .entity(notificationRequestEntity)
                .onError(r -> throwError(r, "Error saving NotificationRequest by request id " + notificationRequestEntity.getRequestId()))
                .execute();

        return findByRequestId(notificationRequestEntity.getRequestId());

    }

    public WriteResponse addAttachment(String documentId, String revision, AttachmentHolder attachment) {
        CouchMediaType mediaType = CouchMediaType.fromString(attachment.getContentType());

        return couchDatabase.put().attachment(
                documentId,
                revision,
                attachment.getName(),
                mediaType,
                attachment.getInputStream())
                .onError(r -> throwError(r, format("Failure storing notification attachment in couch [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
                .execute();
    }

    public AttachmentHolder findAttachment(String requestId, String attachmentName) {
        GetAttachmentResponse attachmentResponse = couchDatabase.get()
                .attachment(requestId, attachmentName)
                .onError(r -> throwError(r, "Error finding NotificationRequest by request id " + requestId))
                .onResponse(r -> throwIfNotFound(r, "NotificationRequest not found by request id " + requestId))
                .execute();

        byte[] content;
        if (attachmentResponse.getContent() instanceof byte[]) {
            content = (byte[]) attachmentResponse.getContent();
        } else {
            content = attachmentResponse.getStringContent().getBytes();
        }

        return new AttachmentHolder(attachmentName,
                attachmentResponse.getContentType().getMediaString(),
                content);
    }

    public NotificationRequestEntity findByRequestId(String requestId) {
        GetEntityResponse<NotificationRequestEntity> getResponse = couchDatabase.get()
                .entity(NotificationRequestEntity.class, requestId)
                .onError(r -> throwError(r, "Error finding NotificationRequest by request id " + requestId))
                .onResponse(r -> throwIfNotFound(r, "NotificationRequest not found by request id " + requestId))
                .execute();

        return getResponse.getFirstEntity();
    }

    public NotificationRequestEntity findByTrackingId(String trackingId) {
        CouchViewQuery viewQuery = CouchViewQuery.builder(CouchConst.REQUEST_DESIGN_NAME, RequestCouchView.ByTrackingId.name())
                .key(trackingId)
                .build();
        GetEntityResponse<NotificationRequestEntity> getResponse = couchDatabase.get()
                .entity(NotificationRequestEntity.class, viewQuery)
                .onError(r -> throwError(r, "Error finding NotificationRequest by tracking id " + trackingId))
                .onResponse(r -> throwIfNotFound(r, "NotificationRequest not found by tracking id " + trackingId))
                .execute();

        return getResponse.getFirstEntity();

    }

    public List<NotificationRequestEntity> findByStatus(NotificationRequestStatus status) {
        return findByStatus(status, 0);
    }

    public List<NotificationRequestEntity> findByStatus(NotificationRequestStatus status, int limit) {

        CouchViewQuery.CouchViewQueryBuilder builder;

        if (status == null) {
            builder = CouchViewQuery.builder(CouchConst.ENTITY, "byEntityType");
            builder.key(NotificationRequestEntity.ENTITY_TYPE);

        } else {
            builder = CouchViewQuery.builder(CouchConst.REQUEST_DESIGN_NAME, RequestCouchView.ByRequestStatusAndCreatedAt.name());

            builder.start(status, null);
            builder.end(status, "Z");
        }

        if (limit > 0) {
            builder.limit(limit);
        }

        CouchViewQuery viewQuery = builder.build();

        GetEntityResponse<NotificationRequestEntity> getResponse = couchDatabase.get()
                .entity(NotificationRequestEntity.class, viewQuery)
                .onError(r -> throwError(r, "Error finding " + status + " requests"))
                .execute();

        return getResponse.getEntityList();
    }

    public void deleteRequest(String requestId) {
        if (requestId == null) {
            throw new NullPointerException("The value \"requestId\" cannot be null.");
        }

        NotificationRequestEntity request = findByRequestId(requestId);

        couchDatabase.delete()
                .document(request.getRequestId(), request.getRevision())
                .onError(r -> throwError(r, format("Error deleting %s with id %s", NotificationRequestEntity.class, request.getRequestId())))
                .execute();
    }

    private void throwError(CouchResponse response, String message) {
        String msg = format("%s: %s", message, response.getErrorReason());
        throw ApiException.internalServerError(msg);
    }

    private void throwIfNotFound(GetEntityResponse response, String message) {
        if (response.isEmpty() || response.isNotFound()) {
            throw ApiException.notFound(message);
        }
    }

    private void throwIfNotFound(GetAttachmentResponse response, String message) {
        if (response.isEmpty() || response.isNotFound()) {
            throw ApiException.notFound(message);
        }
    }
}
