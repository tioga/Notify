package org.tiogasolutions.notify.kernel.task;

import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.task.TaskResponse;
import org.tiogasolutions.notify.pub.task.Task;
import org.springframework.beans.factory.BeanFactory;

public interface TaskProcessor {

  boolean isReady();

  TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task);

  TaskProcessorType getType();

  void init(BeanFactory beanFactory);
}
