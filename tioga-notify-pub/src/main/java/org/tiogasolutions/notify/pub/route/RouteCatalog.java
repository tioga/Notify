package org.tiogasolutions.notify.pub.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteCatalog {
    private static final Logger logger = LoggerFactory.getLogger(RouteCatalog.class);
    private final List<RouteDef> routes;
    private final List<DestinationDef> destinations;

    @JsonCreator
    public RouteCatalog(@JsonProperty("destinations") List<DestinationDef> destinations,
                        @JsonProperty("routes") List<RouteDef> routes) {
        this.destinations = (destinations != null) ? Collections.unmodifiableList(destinations) : Collections.emptyList();
        this.routes = (routes != null) ? Collections.unmodifiableList(routes) : Collections.emptyList();
    }

    public static RouteCatalog newEmptyCatalog() {
        return new RouteCatalog(Collections.emptyList(), Collections.emptyList());
    }

    public Map<String, Destination> mapDestinations() {
        Map<String, Destination> map = new HashMap<>();
        for (DestinationDef destinationDef : getDestinations()) {
            Destination destination = destinationDef.toDestination();
            map.put(destination.getName().toLowerCase(), destination);
        }
        return map;
    }

    public List<Route> loadActiveRoutes() {
        Map<String, Destination> destinationMap = mapDestinations();
        List<Route> routeList = new ArrayList<>();

        for (RouteDef routeDef : getRoutes()) {
            if (routeDef.getRouteStatus() == RouteStatus.ENABLED) {
                List<Destination> routeDestinations = new ArrayList<>();
                for (String destinationName : routeDef.getDestinations()) {
                    Destination destination = destinationMap.get(destinationName.toLowerCase());
                    if (destination == null) {
                        String msg = String.format("The destination \"%s\", specified by the route \"%s\" was not found.", destinationName, routeDef.getName());
                        logger.error(msg);
                    } else if (destination.getDestinationStatus() == DestinationStatus.ENABLED) {
                        routeDestinations.add(destination);
                    }
                }
                Route route = new Route(routeDef.getName(), routeDef.getRouteStatus(), routeDef.getEval(), routeDestinations);
                routeList.add(route);
            }
        }

        return routeList;
    }

    public List<DestinationDef> getDestinations() {
        return destinations;
    }

    public List<RouteDef> getRoutes() {
        return routes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteCatalog that = (RouteCatalog) o;

        if (destinations != null ? !destinations.equals(that.destinations) : that.destinations != null) return false;
        if (routes != null ? !routes.equals(that.routes) : that.routes != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = routes != null ? routes.hashCode() : 0;
        result = 31 * result + (destinations != null ? destinations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RouteCatalog{" +
                "routes=" + routes +
                ", destinations=" + destinations +
                '}';
    }
}
