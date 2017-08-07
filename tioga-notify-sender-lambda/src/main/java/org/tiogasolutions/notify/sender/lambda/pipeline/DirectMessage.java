package org.tiogasolutions.notify.sender.lambda.pipeline;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DirectMessage {

    private final String topic;
    private final String summary;

    public DirectMessage(@JsonProperty("topic") String topic,
                         @JsonProperty("summary") String summary) {
        this.topic = topic;
        this.summary = summary;
    }

    public String getTopic() {
        return topic;
    }

    public String getSummary() {
        return summary;
    }
}
