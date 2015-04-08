package org.lqnotify.kernel.route;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.lqnotify.pub.Notification;
import org.lqnotify.pub.route.Destination;
import org.lqnotify.pub.route.Route;
import org.lqnotify.pub.route.RouteCatalog;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsRouteEvaluator implements RouteEvaluator {
  private final List<RouteMatcher> routeMatcherList;
  private final ScriptEngine engine;
  private final Invocable invocable;

  public JsRouteEvaluator(RouteCatalog routeCatalog) {
    List<Route> routes = routeCatalog.loadActiveRoutes();

    routeMatcherList = new ArrayList<>();
    ScriptEngineManager engineManager = new ScriptEngineManager();
    engine = engineManager.getEngineByName("nashorn");
    invocable = (Invocable)engine;

    routeMatcherList.addAll(routes.stream()
        .map(RouteMatcher::new)
        .collect(Collectors.toList()));
  }

  @Override
  public Set<Destination> findDestinations(Notification notification) {
    Set<Destination> destinations = new HashSet<>();
    routeMatcherList.stream()
        .filter(matcher -> matcher.isMatch(notification))
        .forEach(matcher -> destinations.addAll(matcher.route.getDestinations()));
    return destinations;
  }

  /**
   * TODO - might be more efficient way to do this. Tried to JS with interface but had issues.
   */
  public class RouteMatcher {
    private final Route route;
    private final String jsFunc;

    public RouteMatcher(Route route) {
      this.route = route;
      this.jsFunc = String.format("var eval = %s", route.getEval());
    }

    public boolean isMatch(Notification notification) {
      try {
        engine.eval(jsFunc);
        return (boolean) invocable.invokeFunction("eval", notification.getTopic(), notification.getTraitMap());

      } catch (ScriptException | NoSuchMethodException e) {
        throw ApiException.internalServerError(e);
      }
    }
  }

}
