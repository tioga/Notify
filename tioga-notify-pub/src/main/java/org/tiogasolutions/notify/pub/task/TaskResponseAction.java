package org.tiogasolutions.notify.pub.task;

/**
 * Created by harlan on 3/7/15.
 */
public enum TaskResponseAction {
    COMPLETE("completed"),
    FAIL("failed"),
    RETRY("rescheduled");

    public final String pastTense;

    TaskResponseAction(String pastTense) {
        this.pastTense = pastTense;
    }

    public boolean isComplete() {
        return this == COMPLETE;
    }

    public boolean isFail() {
        return this == FAIL;
    }

    public boolean isRetry() {
        return this == RETRY;
    }
}
