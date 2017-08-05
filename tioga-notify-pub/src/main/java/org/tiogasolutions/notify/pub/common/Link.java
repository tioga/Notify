package org.tiogasolutions.notify.pub.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by harlan on 5/27/15.
 */
public class Link {
    private final String name;
    private final String href;

    public Link(@JsonProperty("name") String name,
                @JsonProperty("href") String href) {
        this.name = name;
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public String getHref() {
        return href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Link that = (Link) o;

        if (href != null ? !href.equals(that.href) : that.href != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (href != null ? href.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Link{" +
                "name='" + name + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
