package org.tiogasolutions.notify.engine;

import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

@Test
public class RootResourceTest extends AbstractEngineJaxRsTest {

    public void testDefaultPage() throws Exception {
        Response response = target("/").request().header("Authorization", toHttpAuth("admin", "Testing123")).get();
        Assert.assertEquals(response.getStatus(), 200);
    }

}