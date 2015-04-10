package org.tiogasolutions.notify.processor.logger;

import org.tiogasolutions.notify.kernel.processor.ProcessorType;
import org.tiogasolutions.notify.pub.DomainProfile;
import org.tiogasolutions.notify.pub.Notification;
import org.tiogasolutions.notify.pub.TaskResponse;
import org.tiogasolutions.notify.kernel.processor.TaskProcessor;
import org.tiogasolutions.notify.pub.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import javax.inject.Named;

@Named
public class LoggerTaskProcessor implements TaskProcessor {

  private static final ProcessorType PROVIDER_TYPE = new ProcessorType("logger");
  private static final Logger log = LoggerFactory.getLogger(LoggerTaskProcessor.class);

  @Override
  public boolean isReady() {
    return true;
  }

  @Override
  public void init(BeanFactory beanFactory) {
  }

  @Override
  public ProcessorType getType() {
    return PROVIDER_TYPE;
  }

  @Override
  public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {
    String message = notification.getSummary();
    log.warn("\n  **\n  ** {}\n  **", message.trim());
    return TaskResponse.complete("Ok");
  }
}
