package org.tiogasolutions.notify.pub.route;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class DestinationDef {
    private final String name;
    private final String provider;
    private DestinationStatus destinationStatus;
    private final Map<String, String> arguments = new LinkedHashMap<>();

    public DestinationDef(@JsonProperty("name") String name,
                          @JsonProperty("provider") String provider) {
        this(name, provider, Collections.emptyMap());
    }

    @JsonCreator
    public DestinationDef(@JsonProperty("name") String name,
                          @JsonProperty("provider") String provider,
                          @JsonProperty("arguments") Map<String, String> arguments) {

        this.name = ExceptionUtils.assertNotZeroLength(name, "name");
        this.provider = ExceptionUtils.assertNotZeroLength(provider, "provider");
        this.destinationStatus = DestinationStatus.ENABLED;

        if (arguments == null) arguments = Collections.emptyMap();
        this.arguments.putAll(arguments);
    }

    public Destination toDestination() {
        return new Destination(name, provider, destinationStatus, arguments);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DestinationDef that = (DestinationDef) o;

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
        return "DestinationDef{" +
                "name='" + name + '\'' +
                ", provider='" + provider + '\'' +
                ", destinationStatus=" + destinationStatus +
                ", arguments=" + arguments +
                '}';
    }

    public DestinationDef addArg(String key, String value) {
        arguments.put(key, value);
        return this;
    }
}
