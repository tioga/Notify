package org.tiogasolutions.notify.sender.lambda.sns;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class SnsEvent {

    private final List<SnsRecord> records;

    public SnsEvent(@JsonProperty("Records") List<SnsRecord> records) {
        this.records = records;
    }

    public List<SnsRecord> getRecords() {
        return records;
    }
}
