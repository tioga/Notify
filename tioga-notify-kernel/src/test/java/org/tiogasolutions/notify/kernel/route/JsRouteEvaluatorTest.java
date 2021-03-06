package org.tiogasolutions.notify.kernel.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.notify.kernel.KernelFixture;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.common.TraitUtil;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;

import static org.testng.Assert.*;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class JsRouteEvaluatorTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final KernelFixture fixture = KernelFixture.it();
    private RouteCatalog routeCatalog;
    private JsonTranslator jsonTranslator;
    private JsRouteEvaluator evaluator;
    private String json;

    @BeforeSuite
    public void beforeSuite() {
        jsonTranslator = fixture.getJsonTranslator();
        json = fixture.readResource("catalog/test-catalog.json");
    }

    @BeforeMethod
    public void beforeMethod() {
        routeCatalog = jsonTranslator.fromJson(RouteCatalog.class, json);
        Notifier notifier = new Notifier(new LoggingNotificationSender());
        evaluator = new JsRouteEvaluator(routeCatalog, notifier);
    }

    public void matchFozzieAndMuppet() throws URISyntaxException {
        Notification notification = newNotification("muppet", "who:fozzie");
        Set<Destination> destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 2);
        boolean found = destinations.stream().anyMatch(d -> d.getName().equals("fozzie"));
        assertTrue(found);
        found = destinations.stream().anyMatch(d -> d.getName().equals("kermit"));
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
        boolean found = destinations.stream().anyMatch(d -> d.getName().equals("fozzie"));
        assertTrue(found);
        found = destinations.stream().anyMatch(d -> d.getName().equals("kermit"));
        assertTrue(found);
    }

    public void matchAnyMuppet() throws URISyntaxException {
        Notification notification = newNotification("muppet", "");
        Set<Destination> destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 2);
        boolean found = destinations.stream().anyMatch(d -> d.getName().equals("kermit"));
        assertTrue(found);
        found = destinations.stream().filter(d -> d.getName().equals("fozzie")).findAny().isPresent();
        assertTrue(found);
    }

    public void hasException() throws URISyntaxException {
        Notification notification = newNotificationWithException("ZZZZ", "YYYY");
        Set<Destination> destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 1);
        boolean found = destinations.stream().anyMatch(d -> d.getName().equals("has_exception"));
        assertTrue(found);
    }

    public void matchCat() throws URISyntaxException {
        Notification notification = newNotification("anything", "pet:cat");
        Set<Destination> destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 1);
        boolean found = destinations.stream().anyMatch(d -> d.getName().equals("cat"));
        assertTrue(found);

        notification = newNotification("anything", "PeT:cAT");
        destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 1);
        found = destinations.stream().anyMatch(d -> d.getName().equals("cat"));
        assertTrue(found);
    }

    public void matchFish() throws URISyntaxException {
        Notification notification = newNotification("anything", "pet:fish");
        Set<Destination> destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 1);
        boolean found = destinations.stream().anyMatch(d -> d.getName().equals("fish"));
        assertTrue(found);

        notification = newNotification("anything", "PeT:FIsh");
        destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 1);
        found = destinations.stream().anyMatch(d -> d.getName().equals("fish"));
        assertTrue(found);
    }

    public void matchNoValue() throws URISyntaxException {
        Notification notification = newNotification("anything", "no_value");
        Set<Destination> destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 1);
        boolean found = destinations.stream().anyMatch(d -> d.getName().equals("no_value"));
        assertTrue(found);

        notification = newNotification("anything", "no_VALUE");
        destinations = evaluator.findDestinations(notification);
        assertNotNull(destinations);
        assertEquals(destinations.size(), 1);
        found = destinations.stream().anyMatch(d -> d.getName().equals("no_value"));
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
        return new Notification(
                false,
                new URI(""),
                "test",
                "999909",
                "3k3k3",
                topic,
                "some summary",
                null,
                ZonedDateTime.now(),
                TraitUtil.toTraitMap(traits),
                Collections.singletonList(new Link("example", "http://example.com")),
                null,
                null);
    }

    private Notification newNotificationWithException(String topic, String traits) throws URISyntaxException {
        Throwable t = new Exception("Some test error");
        ExceptionInfo exceptionInfo = new ExceptionInfo(t);
        return new Notification(
                false,
                new URI(""),
                "test",
                "999909",
                "3k3k3",
                topic,
                "some summary",
                null,
                ZonedDateTime.now(),
                TraitUtil.toTraitMap(traits),
                Collections.singletonList(new Link("example", "http://example.com")),
                exceptionInfo,
                null);
    }
}
