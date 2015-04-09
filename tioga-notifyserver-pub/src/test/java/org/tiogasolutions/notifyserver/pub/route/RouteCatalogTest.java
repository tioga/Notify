package org.tiogasolutions.notifyserver.pub.route;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.notifyserver.pub.PubFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class RouteCatalogTest {
  private final Logger log = LoggerFactory.getLogger(RouteCatalogTest.class);
  private final PubFixture fixture = PubFixture.it();
  private JsonTranslator jsonTranslator;

  @BeforeSuite
  public void setup() {
    jsonTranslator = fixture.getJsonTranslator();
  }

  public void simpleRoundTripTranslationTest() {
    RouteCatalog routeCatalog = fixture.getRouteCatalog();

    String json = jsonTranslator.toJson(routeCatalog);
    log.debug("RoundTrip RouteCatalog JSON: {}", json);

    RouteCatalog translated = jsonTranslator.fromJson(RouteCatalog.class, json);

    assertEquals(translated, routeCatalog);

  }

  public void readJsonText() {
    String json = fixture.readResource("route/catalog.json");

    log.debug("Catalog JSON: {}", json);

    RouteCatalog catalog = jsonTranslator.fromJson(RouteCatalog.class, json);
    assertNotNull(catalog);
  }


}
