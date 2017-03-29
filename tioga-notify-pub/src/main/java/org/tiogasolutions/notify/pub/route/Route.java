package org.tiogasolutions.notify.pub.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ApiBadRequestException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.Collections;
import java.util.List;

public class Route {

    private final String name;
    private final String eval;
    private final RouteStatus routeStatus;
    private final List<Destination> destinations;

    @JsonCreator
    public Route(@JsonProperty("name") String name,
                 @JsonProperty("routeStatus") RouteStatus routeStatus,
                 @JsonProperty("eval") String eval,
                 @JsonProperty("destinations") List<Destination> destinations) {

        this.name = ExceptionUtils.assertNotZeroLength(name, "name", ApiBadRequestException.class, ApiBadRequestException.class);
        this.eval = ExceptionUtils.assertNotZeroLength(eval, "eval", ApiBadRequestException.class, ApiBadRequestException.class);
        this.routeStatus = ExceptionUtils.assertNotNull(routeStatus, "routeStatus", ApiBadRequestException.class);

        this.destinations = (destinations != null) ?
                Collections.unmodifiableList(destinations) :
                Collections.emptyList();
    }

    public RouteStatus getRouteStatus() {
        return routeStatus;
    }

    public String getName() {
        return name;
    }

    public String getEval() {
        return eval;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (name != null ? !name.equals(route.name) : route.name != null) return false;
        if (eval != null ? !eval.equals(route.eval) : route.eval != null) return false;
        if (routeStatus != route.routeStatus) return false;
        return destinations != null ? destinations.equals(route.destinations) : route.destinations == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (eval != null ? eval.hashCode() : 0);
        result = 31 * result + (routeStatus != null ? routeStatus.hashCode() : 0);
        result = 31 * result + (destinations != null ? destinations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Route{" +
                "name='" + name + '\'' +
                ", eval='" + eval + '\'' +
                ", routeStatus=" + routeStatus +
                ", destinations=" + destinations +
                '}';
    }
}
