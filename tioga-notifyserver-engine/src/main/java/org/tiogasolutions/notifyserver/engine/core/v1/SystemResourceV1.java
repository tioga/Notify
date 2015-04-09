package org.tiogasolutions.notifyserver.engine.core.v1;

import org.tiogasolutions.notifyserver.kernel.processor.ProcessorExecutor;
import org.tiogasolutions.notifyserver.kernel.receiver.ReceiverExecutor;

import javax.ws.rs.Path;

/**
 * Created by jacobp on 3/18/2015.
 */
public class SystemResourceV1 {

  private final ReceiverExecutor receiverExecutor;
  private final ProcessorExecutor processorExecutor;

  public SystemResourceV1(ReceiverExecutor receiverExecutor, ProcessorExecutor processorExecutor) {
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
