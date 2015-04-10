package org.tiogasolutions.notify.engine.web;

import org.tiogasolutions.notify.kernel.processor.ProcessorExecutorStatus;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutorStatus;

/**
* Created by jacobp on 3/18/2015.
*/
public class SystemStatus {

  private final ReceiverExecutorStatus receiverExecutorStatus;
  private final ProcessorExecutorStatus processorExecutorStatus;

  public SystemStatus(ReceiverExecutorStatus receiverExecutorStatus, ProcessorExecutorStatus processorExecutorStatus) {
    this.receiverExecutorStatus = receiverExecutorStatus;
    this.processorExecutorStatus = processorExecutorStatus;
  }
  public ReceiverExecutorStatus getReceiverExecutorStatus() {
    return receiverExecutorStatus;
  }
  public ProcessorExecutorStatus getProcessorExecutorStatus() {
    return processorExecutorStatus;
  }
}
