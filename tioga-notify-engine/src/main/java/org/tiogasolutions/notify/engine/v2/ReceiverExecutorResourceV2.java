package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

public class ReceiverExecutorResourceV2 {

    private static final Logger log = LoggerFactory.getLogger(ReceiverExecutorResourceV2.class);

    private final ExecutionManager em;

    public ReceiverExecutorResourceV2(ExecutionManager em) {
        this.em = em;
    }

    @POST
    @Path("/actions/execute")
    public void executeRequestReceiver() {
        log.warn("Receiver explicitly started.");
        em.getReceiverExecutor().execute();
    }
}
