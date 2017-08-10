package org.tiogasolutions.notify.kernel.notification;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.http.CouchMediaType;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.couchace.core.api.response.GetAttachmentResponse;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiBadRequestException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.common.AbstractStore;
import org.tiogasolutions.notify.kernel.common.CouchConst;
import org.tiogasolutions.notify.kernel.request.RequestCouchView;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.attachment.AttachmentHolder;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.pub.notification.NotificationRef;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 10:44 PM
 */
public class NotificationStore extends AbstractStore {

    public NotificationStore(CouchDatabase couchDatabase) {
        super(couchDatabase);
    }

    public NotificationEntity saveAndReload(NotificationEntity entity) {
        ExceptionUtils.assertNotNull(entity, "entity", ApiBadRequestException.class);

        couchDatabase.put()
                .entity(entity)
                .onError(r -> throwError(r, format(SAVE_ENTITY_ERROR, TaskEntity.class, entity.getNotificationId())))
                .execute();

        return findNotificationById(entity.getNotificationId());
    }

    public NotificationEntity findNotificationById(String notificationId) {

        GetEntityResponse<NotificationEntity> getResponse = couchDatabase.get()
                .entity(NotificationEntity.class, notificationId)
                .onError(r -> throwError(r, "Error finding Notification by notification id " + notificationId))
                .onResponse(r -> throwIfNotFound(r, "Notification not found by notification id " + notificationId))
                .execute();

        return getResponse.getFirstEntity();
    }

    public QueryResult<Notification> query(NotificationQuery query) {
        int limit = (query.getLimit() <= 500) ? query.getLimit() : 500;

        // Build the view query.
        CouchViewQuery viewQuery;
        String errorSuffix;
        if (StringUtils.isNotBlank(query.getNotificationId())) {
            NotificationEntity notificationEntity = findNotificationById(query.getNotificationId());
            return ListQueryResult.newSingle(Notification.class, notificationEntity.toNotification());

        } else if (StringUtils.isNotBlank(query.getTrackingId())) {
            errorSuffix = "by tracking id " + query.getTrackingId();
            String trackingId = query.getTrackingId();
            viewQuery = CouchViewQuery.builder(CouchConst.NOTIFICATION_DESIGN_NAME, RequestCouchView.ByTrackingId.name())
                    .key(trackingId)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .build();

        } else if (StringUtils.isNotBlank(query.getTopic())) {
            errorSuffix = "by topic" + query.getTopic();
            String topic = query.getTopic().toLowerCase();
            viewQuery = CouchViewQuery.builder(CouchConst.NOTIFICATION_DESIGN_NAME, NotificationCouchView.ByTopicAndCreatedAt.name())
                    .start(topic, "\\ufff0")
                    .end(topic, null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();

        } else if (StringUtils.isNotBlank(query.getTraitKey()) && StringUtils.isNotBlank(query.getTraitValue())) {
            errorSuffix = String.format("by trait key: %s value: %s", query.getTraitKey(), query.getTraitValue());
            String traitKey = query.getTraitKey().toLowerCase();
            String traitValue = query.getTraitValue().toLowerCase();
            viewQuery = CouchViewQuery.builder(CouchConst.NOTIFICATION_DESIGN_NAME, NotificationCouchView.ByTraitKeyValueAndCreatedAt.name())
                    .start(traitKey, traitValue, "\\ufff0")
                    .end(traitKey, traitValue, null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();

        } else if (StringUtils.isNotBlank(query.getTraitKey())) {
            errorSuffix = String.format("by trait key: %s", query.getTraitKey());
            String traitKey = query.getTraitKey().toLowerCase();
            viewQuery = CouchViewQuery.builder(CouchConst.NOTIFICATION_DESIGN_NAME, NotificationCouchView.ByTraitKeyAndCreatedAt.name())
                    .start(traitKey, "\\ufff0")
                    .end(traitKey, null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();

        } else if (StringUtils.isNotBlank(query.getSummary())) {
            errorSuffix = String.format("by summary: %s", query.getTraitKey());
            String summary = query.getSummary().toLowerCase();
            viewQuery = CouchViewQuery.builder(CouchConst.NOTIFICATION_DESIGN_NAME, NotificationCouchView.BySummary.name())
                    .start(summary)
                    .end("\\ufff0")
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();

        } else {
            // Use created at for query by all.
            errorSuffix = "by created at";
            viewQuery = CouchViewQuery.builder(CouchConst.NOTIFICATION_DESIGN_NAME, NotificationCouchView.ByCreatedAt.name())
                    .start("\\ufff0")
                    .end((Object) null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();
        }

        // Execute the query.
        GetEntityResponse<NotificationEntity> getResponse = couchDatabase.get()
                .entity(NotificationEntity.class, viewQuery)
                .onError(r -> {
                    // We are returning a query result. If not found, return an empty list instead.
                    if (r.isNotFound() == false) throwError(r, "Error finding Notification " + errorSuffix);
                })
                .execute();

        List<Notification> notifications = getResponse.getEntityList()
                .stream()
                .map(NotificationEntity::toNotification)
                .limit(limit)
                .collect(Collectors.toList());

        return ListQueryResult.newResult(Notification.class,
                limit,
                query.getOffset(),
                query.getOffset() + getResponse.getSize(),
                false,
                notifications);
    }

    public NotificationRef createAttachment(CreateAttachment create) {
        CouchMediaType mediaType = CouchMediaType.fromString(create.getContentType());
        NotificationRef notificationRef = create.getNotificationRef();
        WriteResponse response = couchDatabase.put().attachment(
                notificationRef.getNotificationId(),
                notificationRef.getRevision(),
                create.getAttachmentName(),
                mediaType,
                create.getInputStream())
                .onError(r -> throwError(r, format("Failure storing notification attachment in couch [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
                .execute();

        return new NotificationRef(notificationRef.getDomainName(), notificationRef.getNotificationId(), response.getDocumentRevision());
    }

    public AttachmentHolder findAttachment(String notificationId, String attachmentName) {
        GetAttachmentResponse attachmentResponse = couchDatabase.get()
                .attachment(notificationId, attachmentName)
                .onError(r -> throwError(r, "Error finding attachment " + attachmentName))
                .onResponse(r -> throwIfNotFound(r, "Attachment not found " + attachmentName))
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

    public void deleteNotification(String notificationId) {
        ExceptionUtils.assertNotNull(notificationId, "notificationId", ApiBadRequestException.class);

        NotificationEntity notification;

        try {
            notification = findNotificationById(notificationId);

        } catch (ApiNotFoundException e) {
            return; // it's already gone, who cares.
        }

        couchDatabase.delete()
                .document(notification.getNotificationId(), notification.getRevision())
                .onError(r -> throwError(r, format(DELETE_ENTITY_ERROR, TaskEntity.class, notification.getNotificationId())))
                .execute();
    }
}
