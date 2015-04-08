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

public class RequestResourceV1 {

  private final DomainKernel domainKernel;
  private final ExecutionManager executionManager;
  private final EventBus eventBus;

  public RequestResourceV1(ExecutionManager executionManager, DomainKernel domainKernel, EventBus eventBus) {
    this.eventBus = eventBus;
    this.domainKernel = domainKernel;
    this.executionManager = executionManager;
  }

  private DomainProfile getDomainProfile() {
    ExecutionContext ec = executionManager.context();
    return domainKernel.findByApiKey(ec.getApiKey());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public QueryResult<Request> getRequests(@QueryParam("status") LqRequestEntityStatus status) {
    DomainProfile domainProfile = getDomainProfile();
    CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
    LqRequestStore requestStore = new LqRequestStore(requestDb);

    // normally we would make a call like requestEntity.toRequest() but the
    // packaging of these modules do not let to this in that we don't want
    // the extra dependencies in the couch sender.

    List<LqRequestEntity> requestEntities = requestStore.findByStatus(status);
    List<Request> requests = new ArrayList<>();

    requestEntities.forEach((entity)->{
      Request request = LqPubUtils.toRequest(entity);
      requests.add(request);
    });

    return ListQueryResult.newComplete(Request.class, requests);
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Response putRequest(@Context UriInfo uriInfo, LqRequest request) {

    LqRequestEntity requestEntity = LqRequestEntity.newEntity(request);
    CouchDatabase requestDb = domainKernel.requestDb(getDomainProfile());
    requestEntity = new LqRequestStore(requestDb).saveAndReload(requestEntity);
    requestEntity.ready();

    String domainName = executionManager.context().getDomainName();
    eventBus.requestCreated(domainName, requestEntity);

    URI uri = uriInfo.getRequestUriBuilder().path(requestEntity.getRequestId()).build();
    return Response.created(uri).build();
  }

  @Path("simple-entry")
  public SimpleRequestEntryResourceV1 getSimpleRequestEntryV1() {
    return new SimpleRequestEntryResourceV1(executionManager, domainKernel, eventBus);
  }

  @DELETE
  @Path("{requestId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteRequest(@PathParam("requestId") String requestId) {
    DomainProfile domainProfile = getDomainProfile();
    CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
    LqRequestStore requestStore = new LqRequestStore(requestDb);
    requestStore.deleteRequest(requestId);
    return Response.noContent().build();
  }
}
