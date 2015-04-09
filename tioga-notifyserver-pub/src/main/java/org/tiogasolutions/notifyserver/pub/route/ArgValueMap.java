package org.tiogasolutions.notifyserver.pub.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * Created by harlan on 2/28/15.
 */
public class ArgValueMap implements Iterable<Map.Entry<String,ArgValue>>{

  private final Map<String, ArgValue> argMap;

  @JsonCreator
  public ArgValueMap(@JsonProperty("argMap") Map<String, ?> givenMap) {
    Map<String, ArgValue> map = new HashMap<>();
    if (givenMap != null) {
      for(Map.Entry<String, ?> entry : givenMap.entrySet()) {
        map.put(entry.getKey(), new ArgValue(entry.getValue()));
      }
    }
    argMap = Collections.unmodifiableMap(map);
  }

  public boolean isArg(String name, Object other) {
    if (argMap.containsKey(name)) {
      ArgValue value = argMap.get(name);
      return value.isEqual(other);
    } else {
      return false;
    }
  }

  public Object get(String name) {
    ArgValue value = argMap.get(name);
    if (value == null || value.getValue()  == null) {
      return null;
    } else if (value.getValue() instanceof ArgValueMap) {
      return value.asMap();
    } else {
      return value.getValue();
    }
  }

  public Map<String, ArgValue> getArgMap() {
    return argMap;
  }

  public boolean hasArg(String name) {
    return argMap.containsKey(name);
  }

  public ArgValue asValue(String name) {
    return argMap.get(name);
  }

  public String asString(String name) {
    return (argMap.get(name) == null) ? null : argMap.get(name).asString();
  }

  public Boolean asBoolean(String name) {
    return (argMap.get(name) == null) ? null : argMap.get(name).asBoolean();
  }

  public Number asNumber(String name) {
    return (argMap.get(name) == null) ? null : argMap.get(name).asNumber();
  }

  public List<ArgValue> asList(String name) {
    return (argMap.get(name) == null) ? null : argMap.get(name).asList();
  }

  public Map<String, ArgValue> asMap(String name) {
    return (argMap.get(name) == null) ? null : argMap.get(name).asMap();
  }

  public <T> T asEnum(Class<T> type, String name) {
    return (argMap.get(name) == null) ? null : argMap.get(name).asEnum(type);
  }

  @Override
  public Iterator<Map.Entry<String, ArgValue>> iterator() {
    return argMap.entrySet().iterator();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArgValueMap that = (ArgValueMap) o;

    if (argMap != null ? !argMap.equals(that.argMap) : that.argMap != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return argMap != null ? argMap.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "ArgValueMap{" +
        "argMap=" + argMap +
        '}';
  }
}
