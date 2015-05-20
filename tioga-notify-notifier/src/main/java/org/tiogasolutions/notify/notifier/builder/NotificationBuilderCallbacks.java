package org.tiogasolutions.notify.notifier.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:00 AM
 */
public class NotificationBuilderCallbacks {
  private final List<NotificationBuilderCallback> onBeginCallbacks = new ArrayList<>();
  private final List<NotificationBuilderCallback> beforeSendCallbacks = new ArrayList<>();

  public NotificationBuilderCallbacks() {
  }

  public NotificationBuilderCallbacks(NotificationBuilderCallbacks callbacks) {
    onBeginCallbacks.addAll(callbacks.onBeginCallbacks);
    beforeSendCallbacks.addAll(callbacks.beforeSendCallbacks);
  }

  public NotificationBuilderCallbacks copy() {
    return new NotificationBuilderCallbacks(this);
  }

  public void onBegin(NotificationBuilderCallback callback) {
    onBeginCallbacks.add(callback);
  }

  public void onBeforeSend(NotificationBuilderCallback callback) {
    beforeSendCallbacks.add(callback);
  }

  public void callBegin(NotificationBuilder builder) {
    onBeginCallbacks.stream().forEachOrdered(c -> c.call(builder));
  }

  public void callBeforeSend(NotificationBuilder builder) {
    beforeSendCallbacks.stream().forEachOrdered(c -> c.call(builder));
  }


  protected List<NotificationBuilderCallback> getOnBeginCallbacks() {
    return onBeginCallbacks;
  }

  protected List<NotificationBuilderCallback> getBeforeSendCallbacks() {
    return beforeSendCallbacks;
  }
}
