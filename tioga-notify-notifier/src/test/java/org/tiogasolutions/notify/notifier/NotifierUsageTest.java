package org.tiogasolutions.notify.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.notifier.sender.LoggingNotificationSender;

import static org.testng.Assert.assertNotNull;

/**
 * User: Harlan
 * Date: 1/26/2015
 * Time: 11:14 PM
 */
public class NotifierUsageTest {
  private static Logger log = LoggerFactory.getLogger(NotifierUsageTest.class);

  @Test
  public void simpleUsageTest() {
    LoggingNotificationSender sender = new LoggingNotificationSender();
    Notifier notifier = new Notifier(sender);

    notifier.begin()
      .topic("simple")
      .summary("Say something")
      .trackingId("99999")
      .trait("key", "value")
      .link("example", "http://example.com")
      .link("Tioga YouTrack", "http://tioga.myjetbrains.com/")
      .exception(new Exception("Some Trouble"))
      .attach("some-text", "text/plain", "text attach content")
      .send();

    assertNotNull(sender.getLastRequest());
  }

}
