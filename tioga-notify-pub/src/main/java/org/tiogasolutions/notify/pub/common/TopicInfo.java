package org.tiogasolutions.notify.pub.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by harlan on 5/22/15.
 */
public final class TopicInfo {
    private final String name;
    private final long count;

    @JsonCreator
    public TopicInfo(@JsonProperty("name") String name,
                     @JsonProperty("count") long count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }


}
