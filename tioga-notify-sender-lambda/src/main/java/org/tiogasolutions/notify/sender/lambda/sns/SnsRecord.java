package org.tiogasolutions.notify.sender.lambda.sns;

import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class SnsRecord {

    private final Sns sns;
    private final String eventVersion;
    private final String eventSource;
    private final String eventSubscriptionArn;

    public SnsRecord(@JsonProperty("Sns") Sns sns,
                     @JsonProperty("EventVersion") String eventVersion,
                     @JsonProperty("EventSource") String eventSource,
                     @JsonProperty("EventSubscriptionArn") String eventSubscriptionArn) {

        this.sns = sns;
        this.eventVersion = eventVersion;
        this.eventSource = eventSource;
        this.eventSubscriptionArn = eventSubscriptionArn;
    }

    public Sns getSns() {
        return sns;
    }

    public String getEventVersion() {
        return eventVersion;
    }

    public String getEventSource() {
        return eventSource;
    }

    public String getEventSubscriptionArn() {
        return eventSubscriptionArn;
    }
}
