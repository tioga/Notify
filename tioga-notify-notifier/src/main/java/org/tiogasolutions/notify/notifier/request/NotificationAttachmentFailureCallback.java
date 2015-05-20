package org.tiogasolutions.notify.notifier.request;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 12:46 AM
 */
public interface NotificationAttachmentFailureCallback {
  void call(NotificationRequest request, NotificationAttachment attachment, Throwable t);
}
