package org.tiogasolutions.notify.engine.web;

import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutorStatus;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutorStatus;

/**
* Created by jacobp on 3/18/2015.
*/
public class SystemStatus {

  private final ReceiverExecutorStatus receiverExecutorStatus;
  private final TaskProcessorExecutorStatus processorExecutorStatus;

  public SystemStatus(ReceiverExecutorStatus receiverExecutorStatus, TaskProcessorExecutorStatus processorExecutorStatus) {
    this.receiverExecutorStatus = receiverExecutorStatus;
    this.processorExecutorStatus = processorExecutorStatus;
  }
  public ReceiverExecutorStatus getReceiverExecutorStatus() {
    return receiverExecutorStatus;
  }
  public TaskProcessorExecutorStatus getProcessorExecutorStatus() {
    return processorExecutorStatus;
  }
}
