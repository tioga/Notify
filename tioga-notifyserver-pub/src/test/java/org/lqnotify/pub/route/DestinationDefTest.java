package org.lqnotify.pub.route;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.lqnotify.pub.PubFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class DestinationDefTest {
  private final Logger log = LoggerFactory.getLogger(DestinationDefTest.class);
  private final PubFixture fixture = PubFixture.it();
  private JsonTranslator jsonTranslator;

  @BeforeSuite
  public void setup() {
    jsonTranslator = fixture.getJsonTranslator();
  }

  public void simpleRoundTripTranslationTest() {
    DestinationDef destination = new DestinationDef("simple", fixture.getSimpleProviderName(), fixture.getSimpleProviderArgs());

    String json = jsonTranslator.toJson(destination);
    log.debug(json);

    DestinationDef translated = jsonTranslator.fromJson(DestinationDef.class, json);

    assertEquals(translated, destination);

  }

  public void jsonSimpleTest() {
    String json = fixture.readResource("destination/simple.json");

    log.debug(json);

    DestinationDef destination = jsonTranslator.fromJson(DestinationDef.class, json);

    assertEquals(destination.getProvider(), fixture.getSimpleProviderName());
    ArgValueMap argMap = destination.getArgValueMap();
    assertEquals(argMap.asString("recipients"), "a@test.com,b@test.com");
    assertEquals(argMap.asString("message-type"), "email");

  }

  public void jsonAllTypesTest() {
    String json = fixture.readResource("destination/all-types.json");

    log.debug(json);

    DestinationDef destination = jsonTranslator.fromJson(DestinationDef.class, json);

    assertEquals(destination.getName(), "simple");
    assertEquals(destination.getProvider(), fixture.getSimpleProviderName());
    ArgValueMap argMap = destination.getArgValueMap();
    assertEquals(argMap.asString("string-val"), "hello");
    assertEquals(argMap.asNumber("int-val"), 99);
    assertTrue(argMap.asBoolean("bool-val"));
    assertNull(argMap.asString("null-val"));
    assertNotNull(argMap.asList("array-val"));
    assertNotNull(argMap.asMap("map-val"));

  }

}
