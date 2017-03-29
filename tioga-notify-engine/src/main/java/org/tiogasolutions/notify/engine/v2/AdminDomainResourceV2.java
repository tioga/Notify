package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLinks;
import org.tiogasolutions.lib.hal.HalLinksBuilder;
import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class AdminDomainResourceV2 {

    private final PubUtils pubUtils;
    private final ExecutionManager em;
    private final String domainName;

    public AdminDomainResourceV2(PubUtils pubUtils, ExecutionManager em, String domainName) {
        this.pubUtils = pubUtils;
        this.em = em;
        this.domainName = domainName;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainProfile() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);

        HalItem item = pubUtils.fromDomainProfile(HttpStatusCode.CREATED, domainProfile);
        return pubUtils.toResponse(item).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteDomain() {
        em.getDomainKernel().deleteDomain(domainName);

        HalLinks links = HalLinksBuilder.builder()
                .create("domains", pubUtils.uriAdminDomains())
                .build();

        HalItem item = new HalItem(HttpStatusCode.OK, links);
        return pubUtils.toResponse(item).build();
    }

    @GET
    @Path("/summary")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainSummary() {
        DomainSummary summary = em.getDomainKernel().fetchSummary(domainName);
        return Response.ok(summary).build();
    }

    @Path("/notifications")
    public NotificationsResourceV2 getNotificationsResourceV1(@Context Request request) {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new NotificationsResourceV2(request, em);
    }

    @Path("/route-catalog")
    public RouteCatalogResourceV2 getRouteCatalogResourceV1() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new RouteCatalogResourceV2(em);
    }

    @Path("/requests")
    public NotificationRequestResourceV2 getRequestResourceV1() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new NotificationRequestResourceV2(em);
    }

    @Path("/tasks")
    public TasksResourceV2 getTasksResourceV1() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new TasksResourceV2(em);
    }
}
