package org.lqnotify.processor.push;

import com.cosmicpush.gateway.CosmicPushGateway;
import com.cosmicpush.gateway.LiveCosmicPushGateway;
import com.cosmicpush.pub.push.XmppPush;
import org.tiogasolutions.dev.common.EnvUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

// HACK - disabled so JP does not get spammed
@Test(enabled = false)
public class LiveCosmicPushGatewayTest {

  private String url;
  private String username;
  private String password;
  private CosmicPushGateway gateway;

  @BeforeClass
  public void beforeClass() throws Exception {
    url = "http://www.cosmicpush.com/api/v2";
    username = EnvUtils.requireProperty("PUSH_TEST_USERNAME");
    password = EnvUtils.requireProperty("PUSH_TEST_PASSWORD");
    gateway = new LiveCosmicPushGateway(username, password);
  }

  public void testGoogleTalk() throws Exception {
    XmppPush push = XmppPush.newPush("jacob.parr@gmail.com", "This is a test message for Liquid Notifier.", null);
    gateway.send(push);
  }

  public void testPing() throws Exception {
    gateway.ping();
  }
}
