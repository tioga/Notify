package org.tiogasolutions.notifyserver.kernel.receiver;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by harlan on 2/14/15.
 */
// TODO - make schedule an external property and support stop/start
@Named
public class ReceiverKernel {

  private final ReceiverExecutor receiverExecutor;

  @Inject
  public ReceiverKernel(ReceiverExecutor receiverExecutor) {
    this.receiverExecutor = receiverExecutor;

    // TODO - will auto start for now
    startExecutor();
  }

  // TODO - eventually these should become actions (StartReceiverExecutor, StopReceiverExecutor) which can be called from services/resources/api
  public void startExecutor() {
    receiverExecutor.start();
  }

  public void stopExecutor() {
    receiverExecutor.stop();
  }
}
