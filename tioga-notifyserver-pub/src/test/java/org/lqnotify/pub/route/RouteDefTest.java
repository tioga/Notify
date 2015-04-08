package org.lqnotify.pub.route;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.lqnotify.pub.PubFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class RouteDefTest {
  private final Logger log = LoggerFactory.getLogger(RouteDefTest.class);
  private final PubFixture fixture = PubFixture.it();
  private JsonTranslator jsonTranslator;

  @BeforeSuite
  public void setup() {
    jsonTranslator = fixture.getJsonTranslator();
  }

  public void simpleRoundTripTranslationTest() {
    RouteDef routeDef =  new RouteDef("simpleRoute")
        .setRouteStatus(RouteStatus.ENABLED)
        .setEval(fixture.getTrueFunc())
        .addDestination("push");

    String json = jsonTranslator.toJson(routeDef);
    log.debug("RoundTrip RouteDef JSON: {}", json);

    RouteDef translated = jsonTranslator.fromJson(RouteDef.class, json);

    assertEquals(translated, routeDef);

  }

  public void jsonSingleRouteTest() {
    String json = fixture.readResource("route/single-routes.json");

    log.debug("Single Route JSON: {}", json);

    RouteDef route = jsonTranslator.fromJson(RouteDef.class, json);

    assertEquals(route.getName(), "muppet_dog");
    assertEquals(route.getEval(), "function(topic, traits) {return topic = 'muppet' && traits.pet = 'dog'}");
    List<String> destinations = route.getDestinations();
    assertEquals(destinations.size(), 3);
    boolean found = destinations.stream().filter(d -> d.equals("push")).findAny().isPresent();
    assertTrue(found);
    found = destinations.stream().filter(d -> d.equals("logger")).findAny().isPresent();
    assertTrue(found);
    found = destinations.stream().filter(d -> d.equals("fogbugz")).findAny().isPresent();
    assertTrue(found);
  }

}
