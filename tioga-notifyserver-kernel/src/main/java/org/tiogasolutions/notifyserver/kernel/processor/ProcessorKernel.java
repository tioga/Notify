package org.tiogasolutions.notifyserver.kernel.processor;

import org.tiogasolutions.notifyserver.kernel.domain.DomainKernel;
import org.tiogasolutions.notifyserver.kernel.execution.ExecutionManager;
import org.tiogasolutions.notifyserver.kernel.notification.NotificationKernel;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by jacobp on 3/6/2015.
 */
@Named
public class ProcessorKernel {

  private final ProcessorExecutor processorExecutor;

  @Inject
  public ProcessorKernel(ProcessorExecutor processorExecutor, ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel) {
    this.processorExecutor = processorExecutor;

//    new ProcessorExecutor(executionManager, domainKernel, notificationKernel).start();
//    new ProcessorExecutor(executionManager, domainKernel, notificationKernel).start();
//    new ProcessorExecutor(executionManager, domainKernel, notificationKernel).start();
//    new ProcessorExecutor(executionManager, domainKernel, notificationKernel).start();
//    new ProcessorExecutor(executionManager, domainKernel, notificationKernel).start();

    // TODO - will auto start for now
    startExecutor();
  }

  // TODO - eventually these should become actions (StartReceiverExecutor, StopReceiverExecutor) which can be called from services/resources/api
  public void startExecutor() {
    processorExecutor.start();
  }

  public void stopExecutor() {
    processorExecutor.stop();
  }
}
