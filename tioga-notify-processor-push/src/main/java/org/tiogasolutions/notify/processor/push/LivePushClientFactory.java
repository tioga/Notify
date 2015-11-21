package org.tiogasolutions.notify.processor.push;

import org.tiogasolutions.push.client.LivePushServerClient;
import org.tiogasolutions.push.client.PushServerClient;

public class LivePushClientFactory implements PushClientFactory {
  @Override
  public PushServerClient createPushServerClient(String url) {
    return new LivePushServerClient(url);
  }
}
