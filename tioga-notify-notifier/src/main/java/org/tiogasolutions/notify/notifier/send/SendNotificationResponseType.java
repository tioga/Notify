package org.tiogasolutions.notify.notifier.send;

/**
 * Created by harlan on 2/15/15.
 */
public enum SendNotificationResponseType {
    SUCCESS,
    FAILURE;

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public boolean isFailure() {
        return this == FAILURE;
    }
}
