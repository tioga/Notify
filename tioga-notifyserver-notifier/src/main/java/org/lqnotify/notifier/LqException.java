package org.lqnotify.notifier;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 1:43 AM
 */
public class LqException extends RuntimeException {
  public LqException() {
  }

  public LqException(String message) {
    super(message);
  }

  public LqException(String message, Throwable cause) {
    super(message, cause);
  }

  public LqException(Throwable cause) {
    super(cause);
  }

  public LqException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
