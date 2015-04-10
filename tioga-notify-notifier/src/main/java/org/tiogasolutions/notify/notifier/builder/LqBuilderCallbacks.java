package org.tiogasolutions.notify.notifier.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:00 AM
 */
public class LqBuilderCallbacks {
  private final List<LqBuilderCallback> onBeginCallbacks = new ArrayList<>();
  private final List<LqBuilderCallback> beforeSendCallbacks = new ArrayList<>();

  public LqBuilderCallbacks() {
  }

  public LqBuilderCallbacks(LqBuilderCallbacks callbacks) {
    onBeginCallbacks.addAll(callbacks.onBeginCallbacks);
    beforeSendCallbacks.addAll(callbacks.beforeSendCallbacks);
  }

  public LqBuilderCallbacks copy() {
    return new LqBuilderCallbacks(this);
  }

  public void onBegin(LqBuilderCallback callback) {
    onBeginCallbacks.add(callback);
  }

  public void onBeforeSend(LqBuilderCallback callback) {
    beforeSendCallbacks.add(callback);
  }

  public void callBegin(LqBuilder builder) {
    onBeginCallbacks.stream().forEachOrdered(c -> c.call(builder));
  }

  public void callBeforeSend(LqBuilder builder) {
    beforeSendCallbacks.stream().forEachOrdered(c -> c.call(builder));
  }


  protected List<LqBuilderCallback> getOnBeginCallbacks() {
    return onBeginCallbacks;
  }

  protected List<LqBuilderCallback> getBeforeSendCallbacks() {
    return beforeSendCallbacks;
  }
}
