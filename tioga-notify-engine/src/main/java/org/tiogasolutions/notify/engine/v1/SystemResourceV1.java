package org.tiogasolutions.notify.engine.v1;

import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;

import javax.ws.rs.Path;

/**
 * Created by jacobp on 3/18/2015.
 */
public class SystemResourceV1 {

  private final ReceiverExecutor receiverExecutor;
  private final TaskProcessorExecutor processorExecutor;

  public SystemResourceV1(ReceiverExecutor receiverExecutor, TaskProcessorExecutor processorExecutor) {
    this.receiverExecutor = receiverExecutor;
    this.processorExecutor = processorExecutor;
  }

  @Path("/request-receiver")
  public ReceiverExecutorResourceV1 getReceiverExecutorResourceV1() {
    return new ReceiverExecutorResourceV1(receiverExecutor);
  }

  @Path("/task-processor")
  public ProcessorExecutorResourceV1 getProcessorExecutorResourceV1() {
    return new ProcessorExecutorResourceV1(processorExecutor);
  }

}
