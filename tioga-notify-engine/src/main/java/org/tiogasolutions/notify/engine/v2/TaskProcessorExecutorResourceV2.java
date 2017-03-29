package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

public class TaskProcessorExecutorResourceV2 {

    private static final Logger log = LoggerFactory.getLogger(TaskProcessorExecutorResourceV2.class);

    private final ExecutionManager em;

    public TaskProcessorExecutorResourceV2(ExecutionManager em) {
        this.em = em;
    }

    @POST
    @Path("/actions/execute")
    public void executeRequestReceiver() {
        log.warn("Task processor explicitly started.");
        em.getProcessorExecutor().execute();
    }
}
