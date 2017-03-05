package org.tiogasolutions.notify.engine.v1;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLinks;
import org.tiogasolutions.lib.hal.HalLinksBuilder;
import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
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

public class AdminDomainResourceV1 {

    private final PubUtils pubUtils;
    private final DomainKernel domainKernel;
    private final ExecutionManager executionManager;
    private final NotificationKernel notificationKernel;
    private final ReceiverExecutor receiverExecutor;
    private final TaskProcessorExecutor processorExecutor;
    private final EventBus eventBus;
    private final String domainName;

    public AdminDomainResourceV1(PubUtils pubUtils, ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel, ReceiverExecutor receiverExecutor, TaskProcessorExecutor processorExecutor, EventBus eventBus, String domainName) {
        this.pubUtils = pubUtils;
        this.eventBus = eventBus;
        this.domainKernel = domainKernel;
        this.executionManager = executionManager;
        this.notificationKernel = notificationKernel;
        this.receiverExecutor = receiverExecutor;
        this.processorExecutor = processorExecutor;
        this.domainName = domainName;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainProfile() {
        DomainProfile domainProfile = domainKernel.findByDomainName(domainName);

        HalItem item = pubUtils.fromDomainProfile(HttpStatusCode.CREATED, domainProfile);
        return pubUtils.toResponse(item).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteDomain() {
        domainKernel.deleteDomain(domainName);

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
        DomainSummary summary = domainKernel.fetchSummary(domainName);
        return Response.ok(summary).build();
    }

    @Path("/notifications")
    public NotificationsResourceV1 getNotificationsResourceV1(@Context Request request) {
        DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        executionManager.newApiContext(domainProfile);
        return new NotificationsResourceV1(request, executionManager, notificationKernel);
    }

    @Path("/route-catalog")
    public RouteCatalogResourceV1 getRouteCatalogResourceV1() {
        DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        executionManager.newApiContext(domainProfile);
        return new RouteCatalogResourceV1(executionManager, domainKernel);
    }

    @Path("/requests")
    public NotificationRequestResourceV1 getRequestResourceV1() {
        DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        executionManager.newApiContext(domainProfile);
        return new NotificationRequestResourceV1(executionManager, domainKernel, eventBus);
    }

    @Path("/tasks")
    public TasksResourceV1 getTasksResourceV1() {
        DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        executionManager.newApiContext(domainProfile);
        return new TasksResourceV1(executionManager, domainKernel, notificationKernel);
    }
}
