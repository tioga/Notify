package org.lqnotify.notifier.request;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 12:46 AM
 */
public interface LqAttachmentFailureCallback {
  void call(LqRequest request, LqAttachment attachment, Throwable t);
}
