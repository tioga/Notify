package org.lqnotify.kernel.request;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.http.CouchMediaType;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.couchace.core.api.response.CouchResponse;
import org.tiogasolutions.couchace.core.api.response.GetAttachmentResponse;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.couchace.core.api.response.WriteResponse;
import org.lqnotify.notifier.LqException;
import org.lqnotify.notifier.request.LqAttachment;

import javax.inject.Inject;
import java.util.List;

import static java.lang.String.format;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 4:53 PM
 */
public class LqRequestStore {

  private final CouchDatabase couchDatabase;

  @Inject
  public LqRequestStore(CouchDatabase couchDatabase) {
    this.couchDatabase = couchDatabase;
  }

  public CouchDatabase getCouchDatabase() {
    return couchDatabase;
  }

  public WriteResponse save(LqRequestEntity requestEntity) {
    return couchDatabase.put()
        .entity(requestEntity)
        .execute();
  }

  public LqRequestEntity saveAndReload(LqRequestEntity requestEntity) {
    couchDatabase.put()
        .entity(requestEntity)
        .onError(r -> throwError(r, "Error saving LqRequest by request id " + requestEntity.getRequestId()))
        .execute();

    return findByRequestId(requestEntity.getRequestId());

  }

  public WriteResponse addAttachment(String documentId, String revision, LqAttachment attachment) {
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

  public LqAttachmentHolder findAttachment(String requestId, String attachmentName) {
    GetAttachmentResponse attachmentResponse = couchDatabase.get()
        .attachment(requestId, attachmentName)
        .onError(r -> throwError(r, "Error finding LqRequest by request id " + requestId))
        .onResponse(r -> throwIfNotFound(r, "LqRequest not found by request id " + requestId))
        .execute();

    byte[] content;
    if (attachmentResponse.getContent() instanceof byte[]) {
      content = (byte[]) attachmentResponse.getContent();
    } else {
      content = attachmentResponse.getStringContent().getBytes();
    }

    return new LqAttachmentHolder(attachmentName,
        attachmentResponse.getContentType().getMediaString(),
        content);
  }

  public LqRequestEntity findByRequestId(String requestId) {
    GetEntityResponse<LqRequestEntity> getResponse = couchDatabase.get()
        .entity(LqRequestEntity.class, requestId)
        .onError(r -> throwError(r, "Error finding LqRequest by request id " + requestId))
        .onResponse(r -> throwIfNotFound(r, "LqRequest not found by request id " + requestId))
        .execute();

    return getResponse.getFirstEntity();
  }

  public LqRequestEntity findByTrackingId(String trackingId) {
    CouchViewQuery viewQuery = CouchViewQuery.builder(LqCouchSenderConst.REQUEST_DESIGN_NAME, LqRequestCouchView.ByTrackingId.name())
        .key(trackingId)
        .build();
    GetEntityResponse<LqRequestEntity> getResponse = couchDatabase.get()
        .entity(LqRequestEntity.class, viewQuery)
        .onError(r -> throwError(r, "Error finding LqRequest by tracking id " + trackingId))
        .onResponse(r -> throwIfNotFound(r, "LqRequest not found by tracking id " + trackingId))
        .execute();

    return getResponse.getFirstEntity();

  }

  public List<LqRequestEntity> findByStatus(LqRequestEntityStatus status) {

    CouchViewQuery.CouchViewQueryBuilder builder = CouchViewQuery.builder(LqCouchSenderConst.REQUEST_DESIGN_NAME, LqRequestCouchView.ByRequestStatusAndCreatedAt.name());

    if (status != null) {
      builder.start(status, null);
      builder.end(status, "Z");
    }

    CouchViewQuery viewQuery = builder.build();

    GetEntityResponse<LqRequestEntity> getResponse = couchDatabase.get()
        .entity(LqRequestEntity.class, viewQuery)
        .onError(r -> throwError(r, "Error finding " + status + " requests"))
        .execute();

    return getResponse.getEntityList();
  }

  public void deleteRequest(String requestId) {
    if (requestId == null) {
      throw new NullPointerException("The value \"requestId\" cannot be null.");
    }

    LqRequestEntity request = findByRequestId(requestId);

    couchDatabase.delete()
      .document(request.getRequestId(), request.getRevision())
      .onError(r -> throwError(r, format("Error deleting %s with id %s", LqRequestEntity.class, request.getRequestId())))
      .execute();
  }

  private void throwError(CouchResponse response, String message) {
    String msg = format("%s: %s", message, response.getErrorReason());
    throw new LqException(msg);
  }

  private void throwIfNotFound(GetEntityResponse response, String message) {
    if (response.isEmpty() || response.isNotFound()) {
      throw new LqException(message);
    }
  }

  private void throwIfNotFound(GetAttachmentResponse response, String message) {
    if (response.isEmpty() || response.isNotFound()) {
      throw new LqException(message);
    }
  }
}
