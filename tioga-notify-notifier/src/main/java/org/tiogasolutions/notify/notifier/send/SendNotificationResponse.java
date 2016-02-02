package org.tiogasolutions.notify.notifier.send;

/**
 * Created by harlan on 2/15/15.
 */
public final class SendNotificationResponse {
  private final SendNotificationRequest request;
  private final SendNotificationResponseType responseType;
  private final Throwable throwable;

  public static SendNotificationResponse newFailure(SendNotificationRequest request, Throwable throwable) {
   return new SendNotificationResponse(request, SendNotificationResponseType.FAILURE, throwable);
  }

  public static SendNotificationResponse newSuccess(SendNotificationRequest request) {
    return new SendNotificationResponse(request, SendNotificationResponseType.SUCCESS, null);
  }

  public SendNotificationResponse(SendNotificationRequest request, SendNotificationResponseType responseType, Throwable throwable) {
    this.request = request;
    this.responseType = responseType;
    this.throwable = throwable;
  }

  public SendNotificationRequest getRequest() {
    return request;
  }

  public SendNotificationResponseType getResponseType() {
    return responseType;
  }

  public Throwable getThrowable() {
    return throwable;
  }
}
