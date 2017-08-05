package org.tiogasolutions.notify.notifier.send;

import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/25/2015
 * Time: 12:01 AM
 */
public interface NotificationSender {

    Future<SendNotificationResponse> send(SendNotificationRequest request);

    void onResponse(NotificationResponseCallback callback);

    void onSuccess(NotificationResponseCallback callback);

    void onFailure(NotificationResponseCallback callback);

    void onFailure(NotificationAttachmentFailureCallback callback);

}
