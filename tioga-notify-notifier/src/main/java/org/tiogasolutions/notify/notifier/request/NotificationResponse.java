package org.tiogasolutions.notify.notifier.request;

/**
 * Created by harlan on 2/15/15.
 */
public final class NotificationResponse {
  private final NotificationRequest request;
  private final NotificationResponseType responseType;
  private final Throwable throwable;

  public static NotificationResponse newFailure(NotificationRequest request, Throwable throwable) {
   return new NotificationResponse(request, NotificationResponseType.FAILURE, throwable);
  }

  public static NotificationResponse newSuccess(NotificationRequest request) {
    return new NotificationResponse(request, NotificationResponseType.SUCCESS, null);
  }

  public NotificationResponse(NotificationRequest request, NotificationResponseType responseType, Throwable throwable) {
    this.request = request;
    this.responseType = responseType;
    this.throwable = throwable;
  }

  public NotificationRequest getRequest() {
    return request;
  }

  public NotificationResponseType getResponseType() {
    return responseType;
  }

  public Throwable getThrowable() {
    return throwable;
  }
}
