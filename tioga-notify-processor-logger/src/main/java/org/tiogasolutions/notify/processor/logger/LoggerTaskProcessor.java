package org.tiogasolutions.notify.processor.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tiogasolutions.notify.kernel.task.TaskProcessor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorType;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskResponse;

@Component
public class LoggerTaskProcessor implements TaskProcessor {

    private static final TaskProcessorType PROVIDER_TYPE = new TaskProcessorType("logger");
    private static final Logger log = LoggerFactory.getLogger(LoggerTaskProcessor.class);

    public LoggerTaskProcessor() {
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public TaskProcessorType getType() {
        return PROVIDER_TYPE;
    }

    @Override
    public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {
        String message = notification.getSummary();
        log.warn("\n  **\n  ** {}\n  **", message.trim());
        return TaskResponse.complete("Ok");
    }
}
