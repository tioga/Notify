package org.tiogasolutions.notify.engine.v1;

import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by jacobp on 3/6/2015.
 */
public class TaskProcessorExecutorResourceV1 {

  private static final Logger log = LoggerFactory.getLogger(TaskProcessorExecutorResourceV1.class);

  private final TaskProcessorExecutor processorExecutor;

  public TaskProcessorExecutorResourceV1(TaskProcessorExecutor processorExecutor) {
    this.processorExecutor = processorExecutor;
  }

  @POST
  @Path("/actions/execute")
  public void executeRequestReceiver() {
    log.warn("Task processor explicitly started.");
    processorExecutor.execute();
  }
}
