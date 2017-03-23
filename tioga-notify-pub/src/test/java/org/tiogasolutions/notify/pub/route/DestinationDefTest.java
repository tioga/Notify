package org.tiogasolutions.notify.pub.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.notify.pub.PubFixture;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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
        Map<String, String> args = destination.getArguments();
        assertEquals(args.get("recipients"), "a@test.com,b@test.com");
        assertEquals(args.get("message-type"), "email");

    }

    public void jsonAllTypesTest() {
        String json = fixture.readResource("destination/all-types.json");

        log.debug(json);

        DestinationDef destination = jsonTranslator.fromJson(DestinationDef.class, json);

        assertEquals(destination.getName(), "simple");
        assertEquals(destination.getProvider(), fixture.getSimpleProviderName());
        Map<String, String> arguments = destination.getArguments();
        assertEquals(arguments.get("string-val"), "hello");
        assertEquals(arguments.get("int-val"), "99");
        assertEquals(arguments.get("bool-val"), "true");
        assertNull(arguments.get("null-val"));
//    assertNotNull(arguments.asList("array-val"));
//    assertNotNull(arguments.asMap("map-val"));
    }
}
