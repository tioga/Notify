package org.lqnotify.kernel;

import org.lqnotify.kernel.task.TaskEntity;
import org.lqnotify.pub.Notification;

/**
 * Created by jacobp on 3/6/2015.
 */
public interface TaskEventListener {

  void taskCreated(String domainName, TaskEntity task, Notification notification);

}
