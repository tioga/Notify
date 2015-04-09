package org.tiogasolutions.notifyserver.kernel.processor;

import org.tiogasolutions.notifyserver.pub.DomainProfile;
import org.tiogasolutions.notifyserver.pub.Notification;
import org.tiogasolutions.notifyserver.pub.Task;
import org.tiogasolutions.notifyserver.pub.TaskResponse;
import org.springframework.beans.factory.BeanFactory;

public interface TaskProcessor {

  boolean isReady();

  TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task);

  ProcessorType getType();

  void init(BeanFactory beanFactory);
}
