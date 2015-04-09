package org.tiogasolutions.notifyserver.kernel;

import org.tiogasolutions.notifyserver.kernel.task.TaskEntity;
import org.tiogasolutions.notifyserver.pub.Notification;

/**
 * Created by jacobp on 3/6/2015.
 */
public interface TaskEventListener {

  void taskCreated(String domainName, TaskEntity task, Notification notification);

}
