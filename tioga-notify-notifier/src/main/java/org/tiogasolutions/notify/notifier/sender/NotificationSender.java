package org.tiogasolutions.notify.notifier.sender;

import org.tiogasolutions.notify.notifier.request.NotificationRequest;
import org.tiogasolutions.notify.notifier.request.NotificationResponseCallback;
import org.tiogasolutions.notify.notifier.request.NotificationAttachmentFailureCallback;
import org.tiogasolutions.notify.notifier.request.NotificationResponse;

import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/25/2015
 * Time: 12:01 AM
 */
public interface NotificationSender {

  Future<NotificationResponse> send(NotificationRequest request);

  void onResponse(NotificationResponseCallback callback);

  void onSuccess(NotificationResponseCallback callback);

  void onFailure(NotificationResponseCallback callback);

  void onFailure(NotificationAttachmentFailureCallback callback);

}
