package org.tiogasolutions.notify.kernel.processor;

import org.tiogasolutions.notify.pub.DomainProfile;
import org.tiogasolutions.notify.pub.Notification;
import org.tiogasolutions.notify.pub.TaskResponse;
import org.tiogasolutions.notify.pub.Task;
import org.springframework.beans.factory.BeanFactory;

public interface TaskProcessor {

  boolean isReady();

  TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task);

  ProcessorType getType();

  void init(BeanFactory beanFactory);
}
