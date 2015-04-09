package org.tiogasolutions.notifyserver.kernel;

import org.tiogasolutions.notifyserver.kernel.task.TaskEntity;
import org.tiogasolutions.notifyserver.pub.Notification;
import org.tiogasolutions.notifyserver.kernel.request.LqRequestEntity;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Named
public class EventBus {

  private List<TaskEventListener> taskEventListeners = new CopyOnWriteArrayList<>();
  private List<RequestEventListener> requestEventListener = new CopyOnWriteArrayList<>();

  private EventBus() {
  }

  public void subscribe(TaskEventListener listener) {
    taskEventListeners.add(listener);
  }

  public void subscribe(RequestEventListener listener) {
    requestEventListener.add(listener);
  }

  public void taskCreated(String domainName, TaskEntity task, Notification notification) {
    taskEventListeners.forEach((listener) -> listener.taskCreated(domainName, task, notification));
  }

  public void requestCreated(String domainName, LqRequestEntity request) {
    requestEventListener.forEach((listener) -> listener.requestCreated(domainName, request));
  }
}
