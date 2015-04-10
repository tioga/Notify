package org.tiogasolutions.notify.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.notifier.sender.LqSimpleSender;

import static org.testng.Assert.assertNotNull;

/**
 * User: Harlan
 * Date: 1/26/2015
 * Time: 11:14 PM
 */
public class LqNotifierUsageTest {
  private static Logger log = LoggerFactory.getLogger(LqNotifierUsageTest.class);

  @Test
  public void simpleUsageTest() {
    LqSimpleSender sender = new LqSimpleSender();
    LqNotifier notifier = new LqNotifier(sender);

    notifier.begin()
        .topic("simple")
        .summary("Say something")
        .trackingId("99999")
        .trait("key", "value")
        .exception(new Exception("Some Trouble"))
        .attach("some-text", "text/plain", "text attach content")
        .send();

    assertNotNull(sender.getLastRequest());
  }

/*
    @Test
    public void customTest() {
        LqSimpleSender sender = new LqSimpleSender();
        LqNotifier notifier = new LqNotifier(sender);

        notifier.onBegin(b -> b.topic("billing"))
                .onBegin(this::myCustom)
                .onBeforeSend(b -> b.trait("one", "two"));



        notifier.begin()
                .summary("Say something")
                .trackingId("99999")
                .trait("key", "value")
                .exception(new Exception("Some Trouble"))
                .attach("some-text", "text/plain", "text attach content".getBytes(StandardCharsets.UTF_8))
                .send();

        assertNotNull(sender.getLastRequest());
    }

*/

}
