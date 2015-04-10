package org.tiogasolutions.notify.pub.route;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.notify.pub.PubFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class RouteTest {
  private final Logger log = LoggerFactory.getLogger(RouteTest.class);
  private final PubFixture fixture = PubFixture.it();
  private JsonTranslator jsonTranslator;

  @BeforeSuite
  public void setup() {
    jsonTranslator = fixture.getJsonTranslator();
  }

  public void simpleRoundTripTranslationTest() {
    Route route = fixture.getSimpleRoute();

    String json = jsonTranslator.toJson(route);
    log.debug("RoundTrip Route JSON: {}", json);

    Route translated = jsonTranslator.fromJson(Route.class, json);

    assertEquals(translated, route);

  }

}
