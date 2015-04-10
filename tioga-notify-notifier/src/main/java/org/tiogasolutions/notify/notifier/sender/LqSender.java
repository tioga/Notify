package org.tiogasolutions.notify.notifier.sender;

import org.tiogasolutions.notify.notifier.request.LqRequest;
import org.tiogasolutions.notify.notifier.request.LqResponseCallback;
import org.tiogasolutions.notify.notifier.request.LqAttachmentFailureCallback;
import org.tiogasolutions.notify.notifier.request.LqResponse;

import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/25/2015
 * Time: 12:01 AM
 */
public interface LqSender {

  Future<LqResponse> send(LqRequest request);

  void onResponse(LqResponseCallback callback);

  void onSuccess(LqResponseCallback callback);

  void onFailure(LqResponseCallback callback);

  void onFailure(LqAttachmentFailureCallback callback);

}
