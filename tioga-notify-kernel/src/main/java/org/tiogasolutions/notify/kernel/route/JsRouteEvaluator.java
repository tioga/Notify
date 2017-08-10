package org.tiogasolutions.notify.kernel.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.route.Route;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsRouteEvaluator implements RouteEvaluator {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<RouteMatcher> routeMatcherList;
    private final ScriptEngine engine;
    private final Invocable invocable;

    public JsRouteEvaluator(RouteCatalog routeCatalog) {
        List<Route> routes = routeCatalog.loadActiveRoutes();

        routeMatcherList = new ArrayList<>();
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName("nashorn");
        invocable = (Invocable) engine;

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
                return (boolean) invocable.invokeFunction("eval", notification);

            } catch (Exception e) {
                String msg = String.format("Exception testing match (domain=%s, notification=%s, route=%s)", notification.getDomainName(), notification.getNotificationId(), route.getName());
                log.error(msg, e); // TODO - Introduce the notifier here.

                // Don't blow up the world, just return false.
                return false;
            }
        }
    }

}
