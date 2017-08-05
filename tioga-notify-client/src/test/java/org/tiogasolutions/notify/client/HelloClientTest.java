package org.tiogasolutions.notify.client;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HelloClientTest {

    @Test
    public void testSaySomething() throws Exception {
        Assert.assertEquals(new HelloClient().saySomething(), "Hello Client");
    }
}