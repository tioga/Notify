package org.tiogasolutions.notify.notifier;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 1:43 AM
 */
public class NotifierException extends RuntimeException {
    public NotifierException() {
    }

    public NotifierException(String message) {
        super(message);
    }

    public NotifierException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotifierException(Throwable cause) {
        super(cause);
    }

    public NotifierException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
