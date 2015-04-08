package org.lqnotify.pub.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO - bean validation
 * Created by harlan on 2/28/15.
 */
public class RouteDef {
  private final String name;
  private RouteStatus routeStatus;
  private String eval;
  private List<String> destinations = new ArrayList<>();

  @JsonCreator
  public RouteDef(@JsonProperty("name") String name) {
    this.name = name;
    this.routeStatus = RouteStatus.ENABLED;
  }

  public String getName() {
    return name;
  }

  public RouteStatus getRouteStatus() {
    return routeStatus;
  }

  public RouteDef setRouteStatus(RouteStatus routeStatus) {
    this.routeStatus = routeStatus;
    return this;
  }

  public String getEval() {
    return eval;
  }

  public RouteDef setEval(String eval) {
    this.eval = eval;
    return this;
  }

  public List<String> getDestinations() {
    return destinations;
  }

  public RouteDef addDestination(String destination) {
    destinations.add(destination);
    return this;
  }

  public RouteDef setDestinations(List<String> other) {
    destinations.clear();
    if (other != null) {
      destinations.addAll(other);
    }
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RouteDef that = (RouteDef) o;

    if (destinations != null ? !destinations.equals(that.destinations) : that.destinations != null) return false;
    if (eval != null ? !eval.equals(that.eval) : that.eval != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (routeStatus != that.routeStatus) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (routeStatus != null ? routeStatus.hashCode() : 0);
    result = 31 * result + (eval != null ? eval.hashCode() : 0);
    result = 31 * result + (destinations != null ? destinations.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "RouteDef{" +
        "name='" + name + '\'' +
        ", routeStatus=" + routeStatus +
        ", eval='" + eval + '\'' +
        ", destinations=" + destinations +
        '}';
  }
}
