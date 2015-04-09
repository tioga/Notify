package org.tiogasolutions.notifyserver.pub;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notifyserver.pub.route.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by harlan on 2/28/15.
 */
public class PubFixture {
  private static PubFixture it;
  public static PubFixture it() {
    if (it == null) {
      it = new PubFixture();
    }
    return it;
  }
  private static final String SIMPLE_PROVIDER = "SimpleProvider";
  private static final String TRUE_FUNC = "function(topic, traits) {return true;}";

  private final Map<String, Object> simpleProviderArgs;

  private final JsonTranslator jsonTranslator;

  private PubFixture() {

    // Provider args
    Map<String, Object> args = new HashMap<>();
    args.put("type", "email");
    args.put("recipients", Arrays.asList("a@test.com", "b@test.com"));

    simpleProviderArgs = Collections.unmodifiableMap(args);

    jsonTranslator = new TiogaJacksonTranslator();

  }

  public String getSimpleProviderName() {
    return SIMPLE_PROVIDER;
  }

  public Map<String, Object> getSimpleProviderArgs() {
    return simpleProviderArgs;
  }

  public String getTrueFunc() {
    return TRUE_FUNC;
  }

  public JsonTranslator getJsonTranslator() {
    return jsonTranslator;
  }

  public Destination getDestination() {
    return new Destination("simple", getSimpleProviderName(), getSimpleProviderArgs());
  }

  public Route getSimpleRoute() {
    Destination destination = new Destination("simple", getSimpleProviderName(), getSimpleProviderArgs());
    return new Route("simpleRoute", TRUE_FUNC, Collections.singletonList(destination));
  }

  public RouteCatalog getRouteCatalog() {
    List<DestinationDef> destinations = new ArrayList<>();
    destinations.add(new DestinationDef("fozzie", "push").addArg("type", "email").addArg("recipient", "fozzie@muppet.com"));
    destinations.add(new DestinationDef("kermit", "push").addArg("type", "email").addArg("recipient", "kermit@muppet.com"));
    destinations.add(new DestinationDef("dog", "push").addArg("type", "email").addArg("recipient", "dog@pet.com"));
    destinations.add(new DestinationDef("cat", "push").addArg("type", "email").addArg("recipient", "cat@pet.com"));
    List<RouteDef> routes = new ArrayList<>();
    routes.add(new RouteDef("two_muppets").addDestination("fozzie").addDestination("kermit").setEval(TRUE_FUNC));
    routes.add(new RouteDef("one_pet").addDestination("cat").setEval(TRUE_FUNC));
    return new RouteCatalog(destinations, routes);

  }

  public RouteDef getSimpleRouteDef() {
    return new RouteDef("simpleRoute")
        .setRouteStatus(RouteStatus.ENABLED)
        .setEval(TRUE_FUNC)
        .addDestination("push");
  }

  public String readResource(String resourcePath) {
    URL url = getClass().getClassLoader().getResource(resourcePath);
    if (url == null) {
      String msg = String.format("Unable to find file at: %s", resourcePath);
      throw ApiException.badRequest(msg);
    }
    try {
      Path path = Paths.get(url.toURI());
      return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    } catch (URISyntaxException | IOException e) {
      throw ApiException.internalServerError(e);
    }

  }
}
