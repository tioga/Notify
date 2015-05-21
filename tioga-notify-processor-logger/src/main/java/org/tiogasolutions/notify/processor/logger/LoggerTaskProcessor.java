package org.tiogasolutions.notify.processor.logger;

import org.tiogasolutions.notify.kernel.task.TaskProcessorType;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.task.TaskResponse;
import org.tiogasolutions.notify.kernel.task.TaskProcessor;
import org.tiogasolutions.notify.pub.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import javax.inject.Named;

@Named
public class LoggerTaskProcessor implements TaskProcessor {

  private static final TaskProcessorType PROVIDER_TYPE = new TaskProcessorType("logger");
  private static final Logger log = LoggerFactory.getLogger(LoggerTaskProcessor.class);

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void init(BeanFactory beanFactory) {
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
