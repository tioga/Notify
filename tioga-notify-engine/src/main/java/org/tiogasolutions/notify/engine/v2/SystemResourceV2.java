package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.notify.kernel.execution.ExecutionManager;

import javax.ws.rs.Path;

public class SystemResourceV2 {

    private final ExecutionManager em;

    public SystemResourceV2(ExecutionManager em) {
        this.em = em;
    }

    @Path("/request-receiver")
    public ReceiverExecutorResourceV2 getReceiverExecutorResourceV1() {
        return new ReceiverExecutorResourceV2(em);
    }

    @Path("/task-processor")
    public TaskProcessorExecutorResourceV2 getProcessorExecutorResourceV1() {
        return new TaskProcessorExecutorResourceV2(em);
    }

}
