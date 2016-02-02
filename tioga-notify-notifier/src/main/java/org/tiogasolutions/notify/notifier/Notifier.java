package org.tiogasolutions.notify.notifier;

import org.tiogasolutions.notify.notifier.builder.NotificationBuilder;
import org.tiogasolutions.notify.notifier.builder.NotificationBuilderCallback;
import org.tiogasolutions.notify.notifier.builder.NotificationBuilderCallbacks;
import org.tiogasolutions.notify.notifier.send.NotificationSender;

/**
 * User: Harlan
 * Date: 1/26/2015
 * Time: 11:13 PM
 */
public class Notifier {
  private final NotificationSender sender;
  private final NotificationBuilderCallbacks builderCallbacks;

  public Notifier(NotificationSender sender) {
    this.sender = sender;
    builderCallbacks = new NotificationBuilderCallbacks();
  }

  public NotificationBuilder begin() {
    return new NotificationBuilder(sender, builderCallbacks);
  }

  public Notifier onBegin(NotificationBuilderCallback callback) {
    builderCallbacks.onBegin(callback);
    return this;
  }

  public Notifier onBeforeSend(NotificationBuilderCallback callback) {
    builderCallbacks.onBeforeSend(callback);
    return this;
  }


}
