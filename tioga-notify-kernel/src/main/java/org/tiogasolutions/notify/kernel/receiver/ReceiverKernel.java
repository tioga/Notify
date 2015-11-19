package org.tiogasolutions.notify.kernel.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO - make schedule an external property and support stop/start
@Component
public class ReceiverKernel {

  private final ReceiverExecutor receiverExecutor;

  @Autowired
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
