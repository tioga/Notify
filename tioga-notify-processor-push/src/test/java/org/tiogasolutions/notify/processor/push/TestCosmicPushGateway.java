package org.tiogasolutions.notify.processor.push;

import org.tiogasolutions.pushserver.gateway.CosmicPushGateway;
import org.tiogasolutions.pushserver.pub.common.Push;
import org.tiogasolutions.pushserver.pub.common.PushResponse;

public class TestCosmicPushGateway implements CosmicPushGateway {

  /*package*/ Push lastPush;

  @Override
  public long ping() {
      return 0;
  }

  @Override
  public PushResponse send(Push push) {
    this.lastPush = push;
    return null;
  }

  @Override
  @Deprecated
  public PushResponse push(Push push) {
    return send(push);
  }
}
