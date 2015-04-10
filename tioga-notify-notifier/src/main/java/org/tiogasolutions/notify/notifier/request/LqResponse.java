package org.tiogasolutions.notify.notifier.request;

/**
 * Created by harlan on 2/15/15.
 */
public final class LqResponse {
  private final LqRequest request;
  private final LqResponseType responseType;
  private final Throwable throwable;

  public static LqResponse newFailure(LqRequest request, Throwable throwable) {
   return new LqResponse(request, LqResponseType.FAILURE, throwable);
  }

  public static LqResponse newSuccess(LqRequest request) {
    return new LqResponse(request, LqResponseType.SUCCESS, null);
  }

  public LqResponse(LqRequest request, LqResponseType responseType, Throwable throwable) {
    this.request = request;
    this.responseType = responseType;
    this.throwable = throwable;
  }

  public LqRequest getRequest() {
    return request;
  }

  public LqResponseType getResponseType() {
    return responseType;
  }

  public Throwable getThrowable() {
    return throwable;
  }
}
