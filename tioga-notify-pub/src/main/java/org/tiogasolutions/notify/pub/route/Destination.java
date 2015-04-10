package org.tiogasolutions.notify.pub.route;

import com.fasterxml.jackson.annotation.*;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.Map;

/**
 * Created by harlan on 2/28/15.
 */
public class Destination {

  private final String name;
  private final String provider;
  private final DestinationStatus destinationStatus;
  private ArgValueMap argMap;

  public Destination(String name, String provider, Map<String, ?> argMap) {
    this(name, provider, DestinationStatus.ENABLED, argMap);
  }

  @JsonCreator
  public Destination(@JsonProperty("name") String name,
                     @JsonProperty("provider") String provider,
                     @JsonProperty("destinationStatus") DestinationStatus destinationStatus,
                     @JsonProperty("argMap") Map<String, ?> givenArgMap) {
    this.name = ExceptionUtils.assertNotZeroLength(name, "name");
    this.provider = ExceptionUtils.assertNotZeroLength(provider, "provider");
    this.destinationStatus = ExceptionUtils.assertNotNull(destinationStatus, "provider");
    this.argMap = new ArgValueMap(givenArgMap);
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

  /**
   * Used for Json serialization
   * @return Map
   */
/*
  @JsonAnyGetter
  public Map<String,Object> getArgMap() {
    return rawArgMap;
  }
*/

/*
  @JsonAnySetter
  public void addArg(String name, Object value) {
    rawArgMap.put(name, value);
    valueArgMap.put(name, new ArgValue(value));
  }
*/

  @JsonUnwrapped
  public ArgValueMap getArgValueMap() {
    return argMap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Destination that = (Destination) o;

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
    return "Destination{" +
        "name='" + name + '\'' +
        ", provider='" + provider + '\'' +
        ", destinationStatus=" + destinationStatus +
        ", argMap=" + argMap +
        '}';
  }
}
