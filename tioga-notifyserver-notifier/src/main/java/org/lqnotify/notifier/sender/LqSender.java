package org.lqnotify.notifier.sender;

import org.lqnotify.notifier.request.*;

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
