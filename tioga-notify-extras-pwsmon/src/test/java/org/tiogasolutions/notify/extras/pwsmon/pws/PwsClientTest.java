package org.tiogasolutions.notify.extras.pwsmon.pws;

import org.testng.annotations.Test;

@Test
public class PwsClientTest {

  public void testGetEvents() throws Exception {
    PwsClient pwsClient = new PwsClient();
    String content = pwsClient.getApplicatinEvents();
    System.out.println(content);
  }

  public void testLogin() throws Exception {
    PwsClient pwsClient = new PwsClient();
    String content = pwsClient.login("me@jacobparr.com", "go2Pivotal");
    System.out.println(content);
  }
}