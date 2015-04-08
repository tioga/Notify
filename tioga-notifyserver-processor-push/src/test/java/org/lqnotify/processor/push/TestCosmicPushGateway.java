package org.lqnotify.processor.push;

import com.cosmicpush.gateway.CosmicPushGateway;
import com.cosmicpush.pub.common.Push;
import com.cosmicpush.pub.common.PushResponse;

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
