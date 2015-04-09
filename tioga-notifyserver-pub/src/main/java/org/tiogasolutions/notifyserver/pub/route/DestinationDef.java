package org.tiogasolutions.notifyserver.pub.route;

import com.fasterxml.jackson.annotation.*;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harlan on 2/28/15.
 */
public class DestinationDef {
  private final String name;
  private final String provider;
  private DestinationStatus destinationStatus;
  private final Map<String, Object> argMap = new HashMap<>();

  public DestinationDef(String name, String provider, Map<String, Object> argMap) {
    this.name = ExceptionUtils.assertNotZeroLength(name, "name");
    this.provider = ExceptionUtils.assertNotZeroLength(provider, "provider");
    destinationStatus = DestinationStatus.ENABLED;
    if (argMap != null) {
      for(Map.Entry<String, Object> entry : argMap.entrySet()) {
        addArg(entry.getKey(), entry.getValue());
      }
    }
  }

  public Destination toDestination() {
    return new Destination(name, provider, destinationStatus, argMap);
  }

  @JsonCreator
  public DestinationDef(@JsonProperty("name") String name,
                        @JsonProperty("provider") String provider) {
    this.name = ExceptionUtils.assertNotZeroLength(name, "name");
    this.provider = ExceptionUtils.assertNotZeroLength(provider, "provider");
    destinationStatus = DestinationStatus.ENABLED;
  }

  public String getName() {
    return name;
  }

  public String getProvider() {
    return provider;
  }

  public DestinationStatus getDestinationStatus() {
    return destinationStatus;
  }

  public DestinationDef setDestinationStatus(DestinationStatus destinationStatus) {
    this.destinationStatus = destinationStatus;
    return this;
  }

  @JsonIgnore
  public ArgValueMap getArgValueMap() {
    return new ArgValueMap(argMap);
  }

  /**
   * Used for Json serialization
   * @return Map
   */
  @JsonAnyGetter
  public Map<String,Object> getArgMap() {
    return argMap;
  }

  /**
   * Also used for Json deserialization
   * @param name -
   * @param value -
   * @return DestinationDef
   */
  @JsonAnySetter
  public DestinationDef addArg(String name, Object value) {
    argMap.put(name, value);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DestinationDef that = (DestinationDef) o;

    if (argMap != null ? !argMap.equals(that.argMap) : that.argMap != null) return false;
    if (destinationStatus != that.destinationStatus) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (provider != null ? provider.hashCode() : 0);
    result = 31 * result + (destinationStatus != null ? destinationStatus.hashCode() : 0);
    result = 31 * result + (argMap != null ? argMap.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "DestinationDef{" +
        "name='" + name + '\'' +
        ", provider='" + provider + '\'' +
        ", destinationStatus=" + destinationStatus +
        ", argMap=" + argMap +
        '}';
  }
}
