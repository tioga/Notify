package org.tiogasolutions.notify.pub.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Destination {

    private final String name;
    private final String provider;
    private final DestinationStatus destinationStatus;
    private final Map<String, String> arguments = new LinkedHashMap<>();

    public Destination(String name, String provider, Map<String, String> arguments) {
        this(name, provider, DestinationStatus.ENABLED, arguments);
    }

    @JsonCreator
    public Destination(@JsonProperty("name") String name,
                       @JsonProperty("provider") String provider,
                       @JsonProperty("destinationStatus") DestinationStatus destinationStatus,
                       @JsonProperty("arguments") Map<String, String> arguments) {

        this.name = ExceptionUtils.assertNotZeroLength(name, "name");
        this.provider = ExceptionUtils.assertNotZeroLength(provider, "provider");
        this.destinationStatus = ExceptionUtils.assertNotNull(destinationStatus, "provider");

        if (arguments != null) {
            this.arguments.putAll(arguments);
        }
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

    public Map<String, String> getArguments() {
        return arguments;
    }

//    /**
//     * Used for Json serialization
//     *
//     * @return Map
//     */
//  @JsonAnyGetter
//  public Map<String,Object> getArgMap() {
//    return rawArgMap;
//  }

//  @JsonAnySetter
//  public void addArg(String name, Object value) {
//    rawArgMap.put(name, value);
//    valueArgMap.put(name, new ArgValue(value));
//  }

//    @JsonUnwrapped
//    public ArgValueMap getArgValueMap() {
//        return arguments;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Destination that = (Destination) o;

        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;
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
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "name='" + name + '\'' +
                ", provider='" + provider + '\'' +
                ", destinationStatus=" + destinationStatus +
                ", arguments=" + arguments +
                '}';
    }
}
