package org.tiogasolutions.notify.engine.core.v1;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.notify.kernel.EventBus;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.request.LqRequestEntity;
import org.tiogasolutions.notify.kernel.request.LqRequestStore;
import org.tiogasolutions.notify.notifier.request.LqRequest;
import org.tiogasolutions.notify.pub.DomainProfile;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Simplified HTTP request entry where attachments are not supported and request is made ready immediately. Also supports POST
 * for older HTTP clients.
 */
public class SimpleRequestEntryResourceV1 {

  private final DomainKernel domainKernel;
  private final ExecutionManager executionManager;
  private final EventBus eventBus;

  public SimpleRequestEntryResourceV1(ExecutionManager executionManager, DomainKernel domainKernel, EventBus eventBus) {
    this.eventBus = eventBus;
    this.domainKernel = domainKernel;
    this.executionManager = executionManager;
  }

  private DomainProfile getDomainProfile() {
    ExecutionContext ec = executionManager.context();
    return domainKernel.findByApiKey(ec.getApiKey());
  }

  // Need to support POST as PUT is not available in some http clients.
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response postRequest(@Context UriInfo uriInfo, LqRequest request) {

    // TODO - is this something we should support on NotificationDomain?
    CouchDatabase requestDb = domainKernel.requestDb(getDomainProfile());

    // Create and store the request entity
    LqRequestStore store = new LqRequestStore(requestDb);
    LqRequestEntity requestEntity = LqRequestEntity.newEntity(request);
    requestEntity = store.saveAndReload(requestEntity);

    // If it's not ready, make it ready.
    if (requestEntity.getRequestStatus() != LqRequestEntityStatus.READY) {
      requestEntity.ready();
      requestEntity = store.saveAndReload(requestEntity);
    }

    // Generate event for request creation.
    String domainName = executionManager.context().getDomainName();
    eventBus.requestCreated(domainName, requestEntity);

    URI uri = uriInfo.getRequestUriBuilder().path(requestEntity.getRequestId()).build();
    return Response.created(uri).build();
  }

}
