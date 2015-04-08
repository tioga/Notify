package org.lqnotify.engine.core.v1;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.lqnotify.kernel.EventBus;
import org.lqnotify.kernel.LqPubUtils;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.execution.ExecutionContext;
import org.lqnotify.kernel.execution.ExecutionManager;
import org.lqnotify.kernel.request.LqRequestEntity;
import org.lqnotify.kernel.request.LqRequestEntityStatus;
import org.lqnotify.kernel.request.LqRequestStore;
import org.lqnotify.notifier.request.LqRequest;
import org.lqnotify.notifier.request.LqRequestStatus;
import org.lqnotify.pub.DomainProfile;
import org.lqnotify.pub.Request;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
