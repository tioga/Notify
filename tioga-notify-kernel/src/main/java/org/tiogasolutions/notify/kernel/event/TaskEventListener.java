package org.tiogasolutions.notify.kernel.event;

import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.notification.Notification;

/**
 * Created by jacobp on 3/6/2015.
 */
public interface TaskEventListener {

  void taskCreated(String domainName, TaskEntity task, Notification notification);

}
