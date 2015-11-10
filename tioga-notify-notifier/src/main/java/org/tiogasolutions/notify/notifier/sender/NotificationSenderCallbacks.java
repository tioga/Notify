package org.tiogasolutions.notify.notifier.sender;

import org.tiogasolutions.notify.notifier.request.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:00 AM
 */
public class NotificationSenderCallbacks {

  private final List<NotificationResponseCallback> responseCallbacks = new ArrayList<>();
  private final List<NotificationResponseCallback> successCallbacks = new ArrayList<>();
  private final List<NotificationResponseCallback> failureCallbacks = new ArrayList<>();
  private final List<NotificationAttachmentFailureCallback> attachmentFailureCallbacks = new ArrayList<>();

  public void onResponse(NotificationResponseCallback callback) {
    responseCallbacks.add(callback);
  }

  public void onSuccess(NotificationResponseCallback callback) {
    successCallbacks.add(callback);
  }

  public void onFailure(NotificationResponseCallback callback) {
    failureCallbacks.add(callback);
  }

  public void onFailure(NotificationAttachmentFailureCallback callback) {
    attachmentFailureCallbacks.add(callback);
  }

  public void callResponse(NotificationResponse response) {
    responseCallbacks.stream().forEachOrdered(c -> c.call(response));
  }

  public void callSuccess(NotificationResponse response) {
    successCallbacks.stream().forEachOrdered(c -> c.call(response));
    callResponse(response);
  }

  public void callFailure(NotificationResponse response) {
    failureCallbacks.stream().forEachOrdered(c -> c.call(response));
  }

  public void callFailure(NotificationRequest request, NotificationAttachment attachment, Throwable t) {
    attachmentFailureCallbacks.stream().forEachOrdered(c -> c.call(request, attachment, t));
  }

}
