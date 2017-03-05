package org.tiogasolutions.notify.engine.v1;

import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class AdminDomainsResourceV1 {

    private final PubUtils pubUtils;
    private final DomainKernel domainKernel;
    private final ExecutionManager executionManager;
    private final NotificationKernel notificationKernel;
    private final ReceiverExecutor receiverExecutor;
    private final TaskProcessorExecutor processorExecutor;
    private final EventBus eventBus;

    public AdminDomainsResourceV1(PubUtils pubUtils, ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel, ReceiverExecutor receiverExecutor, TaskProcessorExecutor processorExecutor, EventBus eventBus) {
        this.pubUtils = pubUtils;
        this.eventBus = eventBus;
        this.domainKernel = domainKernel;
        this.executionManager = executionManager;
        this.notificationKernel = notificationKernel;
        this.receiverExecutor = receiverExecutor;
        this.processorExecutor = processorExecutor;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainProfiles() {
        List<DomainProfile> domainProfiles = domainKernel.listActiveDomainProfiles();

        HalItem item = pubUtils.fromDomainProfileResults(HttpStatusCode.OK, domainProfiles);
        return pubUtils.toResponse(item).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDomainWithForm(@FormParam("domainName") String domainName) {
        if (StringUtils.isBlank(domainName)) {
            throw ApiException.badRequest("The domain name must be specified.");
        }

        DomainProfile domainProfile = domainKernel.createDomain(domainName);
        HalItem item = pubUtils.fromDomainProfile(HttpStatusCode.CREATED, domainProfile);
        return pubUtils.toResponse(item).build();
    }

    @Path("/{domainName}")
    public AdminDomainResourceV1 getAdminDomainResourceV1(@PathParam("domainName") String domainName) {
        return new AdminDomainResourceV1(pubUtils, executionManager, domainKernel, notificationKernel, receiverExecutor, processorExecutor, eventBus, domainName);
    }
}
