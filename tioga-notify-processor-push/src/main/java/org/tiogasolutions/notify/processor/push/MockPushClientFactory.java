package org.tiogasolutions.notify.processor.push;

import org.tiogasolutions.push.client.MockPushServerClient;
import org.tiogasolutions.push.client.PushServerClient;

public class MockPushClientFactory implements PushClientFactory {

    private final MockPushServerClient lastClient = new MockPushServerClient();

    @Override
    public PushServerClient createPushServerClient(String url) {
        return lastClient;
    }

    public MockPushServerClient getLastClient() {
        return lastClient;
    }
}
