package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class ClientResourceV2 {

    private final ExecutionManager em;


    public ClientResourceV2(ExecutionManager em) {
        this.em = em;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public DomainProfile getDomainProfile() {
        String domainName = em.context().getDomainName();
        return em.getDomainKernel().findByDomainName(domainName);
    }

    @GET
    @Path("summary")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainSummary() {
        try {
            String domainName = em.context().getDomainName();
            DomainSummary summary = em.getDomainKernel().fetchSummary(domainName);
            return Response.ok(summary).build();

        } catch (ApiNotFoundException e) {
            return Response.status(404).entity(e).build();
        }
    }

    @Path("/notifications")
    public NotificationsResourceV2 getNotificationsResource(@Context Request request) {
        return new NotificationsResourceV2(request, em);
    }

    @Path("/route-catalog")
    public RouteCatalogResourceV2 getRouteCatalogResource() {
        return new RouteCatalogResourceV2(em);
    }

    @Path("/requests")
    public NotificationRequestResourceV2 getRequestResourceV1() {
        return new NotificationRequestResourceV2(em);
    }

    @Path("/simple-request-entry")
    public SimpleRequestEntryResourceV2 getSimpleRequestEntryV1() {
        return new SimpleRequestEntryResourceV2(em);
    }
}
