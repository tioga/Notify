package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;

import javax.ws.rs.Path;

/**
 * Created by jacobp on 3/18/2015.
 */
public class SystemResourceV2 {

  private final ReceiverExecutor receiverExecutor;
  private final TaskProcessorExecutor processorExecutor;

  public SystemResourceV2(ReceiverExecutor receiverExecutor, TaskProcessorExecutor processorExecutor) {
    this.receiverExecutor = receiverExecutor;
    this.processorExecutor = processorExecutor;
  }

  @Path("/request-receiver")
  public ReceiverExecutorResourceV2 getReceiverExecutorResourceV1() {
    return new ReceiverExecutorResourceV2(receiverExecutor);
  }

  @Path("/task-processor")
  public TaskProcessorExecutorResourceV2 getProcessorExecutorResourceV1() {
    return new TaskProcessorExecutorResourceV2(processorExecutor);
  }

}
