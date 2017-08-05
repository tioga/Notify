package org.tiogasolutions.notify.kernel.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;

@Component
public class TaskProcessorKernel {

    private final TaskProcessorExecutor processorExecutor;

    @Autowired
    public TaskProcessorKernel(TaskProcessorExecutor processorExecutor, ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel) {
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
