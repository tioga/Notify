package org.lqnotify.notifier.sender;

import org.lqnotify.notifier.request.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:00 AM
 */
public class LqSenderCallbacks {

  private final List<LqResponseCallback> responseCallbacks = new ArrayList<>();
  private final List<LqResponseCallback> successCallbacks = new ArrayList<>();
  private final List<LqResponseCallback> failureCallbacks = new ArrayList<>();
  private final List<LqAttachmentFailureCallback> attachmentFailureCallbacks = new ArrayList<>();

  public void onResponse(LqResponseCallback callback) {
    responseCallbacks.add(callback);
  }

  public void onSuccess(LqResponseCallback callback) {
    successCallbacks.add(callback);
  }

  public void onFailure(LqResponseCallback callback) {
    failureCallbacks.add(callback);
  }

  public void onFailure(LqAttachmentFailureCallback callback) {
    attachmentFailureCallbacks.add(callback);
  }

  public void callResponse(LqResponse response) {
    responseCallbacks.stream().forEachOrdered(c -> c.call(response));
  }

  public void callSuccess(LqResponse response) {
    successCallbacks.stream().forEachOrdered(c -> c.call(response));
    callResponse(response);
  }

  public void callFailure(LqResponse response) {
    failureCallbacks.stream().forEachOrdered(c -> c.call(response));
  }

  public void callFailure(LqRequest request, LqAttachment attachment, Throwable t) {
    attachmentFailureCallbacks.stream().forEachOrdered(c -> c.call(request, attachment, t));
  }

}
