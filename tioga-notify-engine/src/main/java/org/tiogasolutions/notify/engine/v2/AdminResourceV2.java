package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AdminResourceV2 {

    private final PubUtils pubUtils;
    private final DomainKernel domainKernel;
    private final ExecutionManager executionManager;
    private final NotificationKernel notificationKernel;
    private final ReceiverExecutor receiverExecutor;
    private final TaskProcessorExecutor processorExecutor;
    private final EventBus eventBus;

    public AdminResourceV2(PubUtils pubUtils, ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel, ReceiverExecutor receiverExecutor, TaskProcessorExecutor processorExecutor, EventBus eventBus) {
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
    public Response getDefaultPage() {
        return pubUtils.toAdmin().build();
    }

    @Path("/domains")
    public AdminDomainsResourceV2 getDomainProfiles() {
        return new AdminDomainsResourceV2(pubUtils, executionManager, domainKernel, notificationKernel, receiverExecutor, processorExecutor, eventBus);
    }

    @Path("/system")
    public SystemResourceV2 getSystemResourceV1() {
        return new SystemResourceV2(receiverExecutor, processorExecutor);
    }
}
