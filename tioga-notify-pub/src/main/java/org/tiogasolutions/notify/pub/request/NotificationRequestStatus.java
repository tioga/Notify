package org.tiogasolutions.notify.pub.request;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 7:17 PM
 */
public enum NotificationRequestStatus {
    SENDING, READY, PROCESSING, FAILED, COMPLETED;

    public boolean isSending() {
        return this == SENDING;
    }

    public boolean isReady() {
        return this == READY;
    }

    public boolean isProcessing() {
        return this == PROCESSING;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
