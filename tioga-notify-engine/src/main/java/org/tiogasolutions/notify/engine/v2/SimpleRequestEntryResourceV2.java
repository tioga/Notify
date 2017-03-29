package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;

import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Simplified HTTP request entry where attachments are not supported and request is made ready immediately. Also supports POST
 * for older HTTP clients.
 */
public class SimpleRequestEntryResourceV2 {

  private final ExecutionManager em;

  public SimpleRequestEntryResourceV2(ExecutionManager em) {
    this.em = em;
  }

  private DomainProfile getDomainProfile() {
    ExecutionContext ec = em.context();
    return em.getDomainKernel().findByApiKey(ec.getApiKey());
  }

  // Need to support POST as PUT is not available in some http clients.
  @POST
  public Response postRequest(@Context UriInfo uriInfo, NotificationRequest request) {

    // TODO - is this something we should support on NotificationDomain?
    CouchDatabase requestDb = em.getDomainKernel().requestDb(getDomainProfile());

    // Create and store the request entity
    NotificationRequestStore store = new NotificationRequestStore(requestDb);
    NotificationRequestEntity notificationRequestEntity = NotificationRequestEntity.newEntity(request);
    notificationRequestEntity = store.saveAndReload(notificationRequestEntity);

    // If it's not ready, make it ready.
    if (notificationRequestEntity.getRequestStatus() != NotificationRequestStatus.READY) {
      notificationRequestEntity.ready();
      notificationRequestEntity = store.saveAndReload(notificationRequestEntity);
    }

    // Generate event for request creation.
    String domainName = em.context().getDomainName();
    em.getEventBus().requestCreated(domainName, notificationRequestEntity);

    URI uri = uriInfo.getRequestUriBuilder().path(notificationRequestEntity.getRequestId()).build();
    return Response.created(uri).build();
  }

}
