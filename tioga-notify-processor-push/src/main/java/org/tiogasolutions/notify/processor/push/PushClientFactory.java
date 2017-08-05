package org.tiogasolutions.notify.processor.push;

import org.tiogasolutions.push.client.PushServerClient;

public interface PushClientFactory {
    public PushServerClient createPushServerClient(String url);
}
