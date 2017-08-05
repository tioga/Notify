package org.tiogasolutions.notify.sender.lambda.pub.sns;

import com.fasterxml.jackson.annotation.JsonProperty;

// @JsonIgnoreProperties(ignoreUnknown = true)
public class MessageAttribute {

    private final String type;
    private final String value;

    public MessageAttribute(@JsonProperty("Type") String type,
                            @JsonProperty("Value") String value) {

        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
