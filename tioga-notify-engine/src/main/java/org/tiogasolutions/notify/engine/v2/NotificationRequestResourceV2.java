package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NotificationRequestResourceV2 {

    private final ExecutionManager em;

    public NotificationRequestResourceV2(ExecutionManager em) {
        this.em = em;
    }

    private DomainProfile getDomainProfile() {
        ExecutionContext ec = em.context();
        return em.getDomainKernel().findByApiKey(ec.getApiKey());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public QueryResult<NotificationRequest> getRequests(@QueryParam("status") NotificationRequestStatus status) {
        DomainProfile domainProfile = getDomainProfile();
        CouchDatabase requestDb = em.getDomainKernel().requestDb(domainProfile);
        NotificationRequestStore requestStore = new NotificationRequestStore(requestDb);

        // normally we would make a call like requestEntity.toRequest() but the
        // packaging of these modules do not let to this in that we don't want
        // the extra dependencies in the couch sender.

        List<NotificationRequestEntity> requestEntities = requestStore.findByStatus(status);
        List<NotificationRequest> requests = new ArrayList<>();

        requestEntities.forEach((entity) -> {
            requests.add(entity.toRequest());
        });

        return ListQueryResult.newComplete(NotificationRequest.class, requests);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response putRequest(@Context UriInfo uriInfo, NotificationRequest request) {

        CouchDatabase requestDb = em.getDomainKernel().requestDb(getDomainProfile());
        NotificationRequestStore store = new NotificationRequestStore(requestDb);

        NotificationRequestEntity notificationRequestEntity = NotificationRequestEntity.newEntity(request);
        notificationRequestEntity.ready();
        notificationRequestEntity = store.saveAndReload(notificationRequestEntity);

        String domainName = em.context().getDomainName();
        em.getEventBus().requestCreated(domainName, notificationRequestEntity);

        URI uri = uriInfo.getRequestUriBuilder().path(notificationRequestEntity.getRequestId()).build();
        return Response.created(uri).entity(notificationRequestEntity).build();
    }

    @Path("simple-entry")
    public SimpleRequestEntryResourceV2 getSimpleRequestEntryV1() {
        return new SimpleRequestEntryResourceV2(em);
    }

    @DELETE
    @Path("{requestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRequest(@PathParam("requestId") String requestId) {
        DomainProfile domainProfile = getDomainProfile();
        CouchDatabase requestDb = em.getDomainKernel().requestDb(domainProfile);
        NotificationRequestStore requestStore = new NotificationRequestStore(requestDb);
        requestStore.deleteRequest(requestId);
        return Response.noContent().build();
    }
}
