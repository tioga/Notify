package org.tiogasolutions.notifyserver.engine.core;

import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;

// HACK - disabled when we started using static content because we don't really have anywhere to read the default page. - HN
// @Test(enabled = false)
public class EngRootResourceTest /*extends AbstractEngineJaxRsTest*/ {

  public void testDefaultPage() throws Exception {
//    Response response = target().request().get();
//    assertEquals(response.getStatus(), 200);
  }
}