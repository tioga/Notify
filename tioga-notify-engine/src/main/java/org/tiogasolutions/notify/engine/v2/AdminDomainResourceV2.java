package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLinks;
import org.tiogasolutions.lib.hal.HalLinksBuilder;
import org.tiogasolutions.notify.engine.jobs.PruneNotificationsAndTasksJob;
import org.tiogasolutions.notify.engine.jobs.PruneRequestsJob;
import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

public class AdminDomainResourceV2 {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final PubUtils pubUtils;
    private final ExecutionManager em;
    private final String domainName;

    private static final Object PRUNE_NOTIFICATIONS_AND_TASKS_LOCK = new Object();
    private PruneNotificationsAndTasksJob pruneNotificationsAndTasksJob;

    private static final Object PRUNE_REQUESTS_LOCK = new Object();
    private PruneRequestsJob pruneRequestsJob;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/prune-requests")
    public Response getPruneRequests() {
        synchronized (PRUNE_REQUESTS_LOCK) {
            if (pruneRequestsJob == null) {
                throw ApiException.notFound("The job to prune requests is not running.");

            } else {
                return Response.accepted(pruneRequestsJob.getResults()).build();
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/prune-requests")
    public Response pruneRequests() {
        synchronized (PRUNE_REQUESTS_LOCK) {
            if (pruneRequestsJob == null || pruneRequestsJob.isRunning() == false) {
                pruneRequestsJob = new PruneRequestsJob(em.getDomainKernel(), domainName);
                new Thread(pruneRequestsJob).start();
                return Response.accepted(pruneRequestsJob.getStartedResults()).build();

            } else {
                throw ApiException.conflict("The job to prune requests is already running.");
            }
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/prune-notifications")
    public Response getPruneNotifications() {
        synchronized (PRUNE_NOTIFICATIONS_AND_TASKS_LOCK) {
            if (pruneNotificationsAndTasksJob == null) {
                throw ApiException.notFound("The job to prune notifications and tasks is not running.");

            } else {
                return Response.accepted(pruneNotificationsAndTasksJob.getResults()).build();
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/jobs/prune-notifications")
    public Response pruneNotifications() {
        synchronized (PRUNE_NOTIFICATIONS_AND_TASKS_LOCK) {
            if (pruneNotificationsAndTasksJob == null || pruneNotificationsAndTasksJob.isRunning() == false) {
                pruneNotificationsAndTasksJob = new PruneNotificationsAndTasksJob(em.getDomainKernel(), domainName);
                new Thread(pruneNotificationsAndTasksJob).start();
                return Response.accepted(pruneNotificationsAndTasksJob.getStartedResults()).build();

            } else {
                throw ApiException.conflict("The job to prune notifications and tasks is already running.");
            }
        }
    }
}
