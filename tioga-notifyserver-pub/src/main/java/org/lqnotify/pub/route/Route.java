package org.lqnotify.pub.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Created by harlan on 2/28/15.
 */
public class Route {
  private final String name;

  private final String eval;
  private final List<Destination> destinations;

  @JsonCreator
  public Route(@JsonProperty("name") String name,
               @JsonProperty("eval") String eval,
               @JsonProperty("destinations") List<Destination> destinations) {
    this.name = name;
    this.eval = eval;
    this.destinations = (destinations != null) ? Collections.unmodifiableList(destinations) : Collections.emptyList();
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

    if (!destinations.equals(route.destinations)) return false;
    if (eval != null ? !eval.equals(route.eval) : route.eval != null) return false;
    if (!name.equals(route.name)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + (eval != null ? eval.hashCode() : 0);
    result = 31 * result + destinations.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Route{" +
        "name='" + name + '\'' +
        ", eval='" + eval + '\'' +
        ", destinations=" + destinations +
        '}';
  }
}
