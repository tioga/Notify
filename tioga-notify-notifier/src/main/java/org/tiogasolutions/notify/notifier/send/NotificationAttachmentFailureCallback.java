package org.tiogasolutions.notify.notifier.send;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 12:46 AM
 */
public interface NotificationAttachmentFailureCallback {
    void call(SendNotificationRequest request, NotificationAttachment attachment, Throwable t);
}
