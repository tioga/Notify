package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by jacobp on 3/6/2015.
 */
public class ReceiverExecutorResourceV2 {

  private static final Logger log = LoggerFactory.getLogger(ReceiverExecutorResourceV2.class);

  private final ReceiverExecutor receiverExecutor;

  public ReceiverExecutorResourceV2(ReceiverExecutor receiverExecutor) {
    this.receiverExecutor = receiverExecutor;
  }

  @POST
  @Path("/actions/execute")
  public void executeRequestReceiver() {
    log.warn("Receiver explicitly started.");
    receiverExecutor.execute();
  }
}
