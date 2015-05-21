package org.tiogasolutions.notify.kernel.route;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.notify.notifier.builder.NotificationTrait;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.kernel.KernelFixture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class JsRouteEvaluatorTest {
  private static final Logger log = LoggerFactory.getLogger(JsRouteEvaluatorTest.class);
  private final KernelFixture fixture = KernelFixture.it();
  private RouteCatalog routeCatalog;
  private JsonTranslator jsonTranslator;
  private JsRouteEvaluator evaluator;


  @BeforeSuite
  public void setup() {
    jsonTranslator = fixture.getJsonTranslator();
    String json = fixture.readResource("catalog/test-catalog.json");

    log.info("Catalog JSON: {}", json);

    routeCatalog = jsonTranslator.fromJson(RouteCatalog.class, json);
    evaluator = new JsRouteEvaluator(routeCatalog);
  }

  public void matchFozzieAndMuppet() throws URISyntaxException {
    Notification notification = newNotification("muppet", "who:fozzie");
    Set<Destination> destinations = evaluator.findDestinations(notification);
    assertNotNull(destinations);
    assertEquals(destinations.size(), 2);
    boolean found = destinations.stream().filter(d -> d.getName().equals("fozzie")).findAny().isPresent();
    assertTrue(found);
    found = destinations.stream().filter(d -> d.getName().equals("kermit")).findAny().isPresent();
    assertTrue(found);
  }

  public void matchFozzieWithoutMuppet() throws URISyntaxException {
    Notification notification = newNotification("junk", "who:fozzie");
    Set<Destination> destinations = evaluator.findDestinations(notification);
    assertNotNull(destinations);
    assertEquals(destinations.size(), 0);
  }

  public void matchKermit() throws URISyntaxException {
    Notification notification = newNotification("muppet", "who:kermit");
    Set<Destination> destinations = evaluator.findDestinations(notification);
    assertNotNull(destinations);
    assertEquals(destinations.size(), 2);
    boolean found = destinations.stream().filter(d -> d.getName().equals("fozzie")).findAny().isPresent();
    assertTrue(found);
    found = destinations.stream().filter(d -> d.getName().equals("kermit")).findAny().isPresent();
    assertTrue(found);
  }

  public void matchAnyMuppet() throws URISyntaxException {
    Notification notification = newNotification("muppet", "");
    Set<Destination> destinations = evaluator.findDestinations(notification);
    assertNotNull(destinations);
    assertEquals(destinations.size(), 2);
    boolean found = destinations.stream().filter(d -> d.getName().equals("kermit")).findAny().isPresent();
    assertTrue(found);
    found = destinations.stream().filter(d -> d.getName().equals("fozzie")).findAny().isPresent();
    assertTrue(found);
  }

  public void matchCat() throws URISyntaxException {
    Notification notification = newNotification("anything", "pet:cat");
    Set<Destination> destinations = evaluator.findDestinations(notification);
    assertNotNull(destinations);
    assertEquals(destinations.size(), 1);
    boolean found = destinations.stream().filter(d -> d.getName().equals("cat")).findAny().isPresent();
    assertTrue(found);
  }

  public void noMatchDog() throws URISyntaxException {
    Notification notification = newNotification("anything", "pet:dog");
    Set<Destination> destinations = evaluator.findDestinations(notification);
    assertNotNull(destinations);
    assertEquals(destinations.size(), 0);
  }

  public void noMatchJuk() throws URISyntaxException {
    Notification notification = newNotification("junk", "");
    Set<Destination> destinations = evaluator.findDestinations(notification);
    assertNotNull(destinations);
    assertEquals(destinations.size(), 0);
  }
  private Notification newNotification(String topic, String traits) throws URISyntaxException {
    return new Notification(new URI(""),
        "test",
        "999909",
        "3k3k3",
        topic,
        "some summary",
        null,
        ZonedDateTime.now(),
        NotificationTrait.toTraitMap(traits),
        null,
        null);
  }

}
