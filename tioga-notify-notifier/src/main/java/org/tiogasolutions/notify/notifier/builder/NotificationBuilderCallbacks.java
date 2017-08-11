package org.tiogasolutions.notify.notifier.builder;

import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;

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
    private final List<SendNotificationResponseCallback> onSuccessCallbacks = new ArrayList<>();
    private final List<SendNotificationResponseCallback> onFailureCallbacks = new ArrayList<>();

    public NotificationBuilderCallbacks() {
    }

    public NotificationBuilderCallbacks(NotificationBuilderCallbacks callbacks) {
        onBeginCallbacks.addAll(callbacks.onBeginCallbacks);
        beforeSendCallbacks.addAll(callbacks.beforeSendCallbacks);
        onSuccessCallbacks.addAll(callbacks.onSuccessCallbacks);
        onFailureCallbacks.addAll(callbacks.onFailureCallbacks);
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

    public void onSuccess(SendNotificationResponseCallback callback) {
        onSuccessCallbacks.add(callback);
    }

    public void onFailure(SendNotificationResponseCallback callback) {
        onFailureCallbacks.add(callback);
    }



    public void callBegin(NotificationBuilder builder) {
        onBeginCallbacks.forEach(c -> c.call(builder));
    }

    public void callBeforeSend(NotificationBuilder builder) {
        beforeSendCallbacks.forEach(c -> c.call(builder));
    }

    public void callOnSuccess(SendNotificationResponse response) {
        onSuccessCallbacks.forEach(c -> c.call(response));
    }

    public void callOnFailure(SendNotificationResponse response) {
        onFailureCallbacks.forEach(c -> c.call(response));
    }



    protected List<NotificationBuilderCallback> getOnBeginCallbacks() {
        return onBeginCallbacks;
    }

    protected List<NotificationBuilderCallback> getBeforeSendCallbacks() {
        return beforeSendCallbacks;
    }

    protected List<SendNotificationResponseCallback> getOnSuccessCallbacks() {
        return onSuccessCallbacks;
    }

    protected List<SendNotificationResponseCallback> getOnFailureCallbacks() {
        return onFailureCallbacks;
    }
}
