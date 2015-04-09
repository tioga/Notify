package org.tiogasolutions.notifyserver.notifier.sender;

import org.tiogasolutions.notifyserver.notifier.request.LqAttachmentFailureCallback;
import org.tiogasolutions.notifyserver.notifier.request.LqResponseCallback;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:15 AM
 */
public abstract class LqAbstractSender implements LqSender {
  protected final LqSenderCallbacks callbacks = new LqSenderCallbacks();

  @Override
  public void onResponse(LqResponseCallback callback) {
    callbacks.onResponse(callback);
  }

  @Override
  public void onSuccess(LqResponseCallback callback) {
    callbacks.onSuccess(callback);
  }

  @Override
  public void onFailure(LqResponseCallback callback) {
    callbacks.onFailure(callback);
  }

  @Override
  public void onFailure(LqAttachmentFailureCallback callback) {
    callbacks.onFailure(callback);
  }

}
