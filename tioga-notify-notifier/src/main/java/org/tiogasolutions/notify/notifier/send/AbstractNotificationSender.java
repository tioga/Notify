package org.tiogasolutions.notify.notifier.send;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:15 AM
 */
public abstract class AbstractNotificationSender implements NotificationSender {
  protected final NotificationSenderCallbacks callbacks = new NotificationSenderCallbacks();

  @Override
  public void onResponse(NotificationResponseCallback callback) {
    callbacks.onResponse(callback);
  }

  @Override
  public void onSuccess(NotificationResponseCallback callback) {
    callbacks.onSuccess(callback);
  }

  @Override
  public void onFailure(NotificationResponseCallback callback) {
    callbacks.onFailure(callback);
  }

  @Override
  public void onFailure(NotificationAttachmentFailureCallback callback) {
    callbacks.onFailure(callback);
  }

}
