package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;

import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

public class SystemResourceV2 {

    private final ExecutionManager em;
    private final DomainKernel domainKernel;

    public SystemResourceV2(ExecutionManager em, DomainKernel domainKernel) {
        this.em = em;
        this.domainKernel = domainKernel;
    }

    @Path("/request-receiver")
    public ReceiverExecutorResourceV2 getReceiverExecutorResourceV1() {
        return new ReceiverExecutorResourceV2(em);
    }

    @Path("/task-processor")
    public TaskProcessorExecutorResourceV2 getProcessorExecutorResourceV1() {
        return new TaskProcessorExecutorResourceV2(em);
    }

    @Path("/jobs/prune/requests")
    public Response pruneRequests(@FormParam("domainName") String domainName) {
        DomainProfile domainProfile = domainKernel.findByDomainName(domainName);

        CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
        NotificationRequestStore requestStore = new NotificationRequestStore(requestDb);

        List<NotificationRequestEntity> requests = requestStore.findByStatus(NotificationRequestStatus.COMPLETED);
        for (NotificationRequestEntity request : requests) {
            requestStore.deleteRequest(request.getRequestId());
        }

        return Response.ok().build();
    }
}
