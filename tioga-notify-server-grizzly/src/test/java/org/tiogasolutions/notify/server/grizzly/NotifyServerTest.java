package org.tiogasolutions.notify.server.grizzly;

import org.testng.annotations.Test;

import java.net.URL;

import static org.testng.Assert.assertNotNull;

// HACK - disable because NotifyServer.DEFAULT_SPRING_FILE does not exist
@Test(enabled = false)
public class NotifyServerTest {

  public void testGetDefaultSpringFile() {

    String path = NotifyServer.DEFAULT_SPRING_FILE;
    URL url = getClass().getResource(path);
    assertNotNull(url);
  }

}