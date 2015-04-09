package org.tiogasolutions.notifyserver.engine.core.v1;

import org.tiogasolutions.notifyserver.kernel.processor.ProcessorExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by jacobp on 3/6/2015.
 */
public class ProcessorExecutorResourceV1 {

  private static final Logger log = LoggerFactory.getLogger(ProcessorExecutorResourceV1.class);

  private final ProcessorExecutor processorExecutor;

  public ProcessorExecutorResourceV1(ProcessorExecutor processorExecutor) {
    this.processorExecutor = processorExecutor;
  }

  @POST
  @Path("/actions/execute")
  public void executeRequestReceiver() {
    log.warn("Task processor explicitly started.");
    processorExecutor.execute();
  }
}
