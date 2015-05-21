package org.tiogasolutions.notify.engine.v1;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.common.NotifyConversionUtils;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntityStatus;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.notifier.request.NotificationRequest;
import org.tiogasolutions.notify.pub.DomainProfile;
import org.tiogasolutions.notify.pub.Request;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;

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
  public QueryResult<Request> getRequests(@QueryParam("status") NotificationRequestEntityStatus status) {
    DomainProfile domainProfile = getDomainProfile();
    CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
    NotificationRequestStore requestStore = new NotificationRequestStore(requestDb);

    // normally we would make a call like requestEntity.toRequest() but the
    // packaging of these modules do not let to this in that we don't want
    // the extra dependencies in the couch sender.

    List<NotificationRequestEntity> requestEntities = requestStore.findByStatus(status);
    List<Request> requests = new ArrayList<>();

    requestEntities.forEach((entity)->{
      Request request = NotifyConversionUtils.toRequest(entity);
      requests.add(request);
    });

    return ListQueryResult.newComplete(Request.class, requests);
  }

  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  public Response putRequest(@Context UriInfo uriInfo, NotificationRequest request) {

    NotificationRequestEntity notificationRequestEntity = NotificationRequestEntity.newEntity(request);
    CouchDatabase requestDb = domainKernel.requestDb(getDomainProfile());
    notificationRequestEntity = new NotificationRequestStore(requestDb).saveAndReload(notificationRequestEntity);
    notificationRequestEntity.ready();

    String domainName = executionManager.context().getDomainName();
    eventBus.requestCreated(domainName, notificationRequestEntity);

    URI uri = uriInfo.getRequestUriBuilder().path(notificationRequestEntity.getRequestId()).build();
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
    NotificationRequestStore requestStore = new NotificationRequestStore(requestDb);
    requestStore.deleteRequest(requestId);
    return Response.noContent().build();
  }
}
