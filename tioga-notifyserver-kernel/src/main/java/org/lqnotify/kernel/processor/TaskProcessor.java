package org.lqnotify.kernel.processor;

import org.lqnotify.pub.DomainProfile;
import org.lqnotify.pub.Notification;
import org.lqnotify.pub.Task;
import org.lqnotify.pub.TaskResponse;
import org.springframework.beans.factory.BeanFactory;

public interface TaskProcessor {

  boolean isReady();

  TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task);

  ProcessorType getType();

  void init(BeanFactory beanFactory);
}
