package org.lqnotify.pub.route;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.lqnotify.pub.PubFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class DestinationTest {
  private final Logger log = LoggerFactory.getLogger(DestinationTest.class);
  private final PubFixture fixture = PubFixture.it();
  private JsonTranslator jsonTranslator;

  @BeforeSuite
  public void setup() {
    jsonTranslator = fixture.getJsonTranslator();
  }

  public void roundTripTranslationTest() {
    Destination destination = new Destination("simple", fixture.getSimpleProviderName(), fixture.getSimpleProviderArgs());

    String json = jsonTranslator.toJson(destination);
    log.debug(json);

    Destination translated = jsonTranslator.fromJson(Destination.class, json);

    assertEquals(translated, destination);

  }

}
