package org.tiogasolutions.notify.sender.lambda.build;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CodeBuildStateChangeMsg {

    private final String version;
    private final String id;
    private final String detailType;
    private final String source;
    private final String account;
    private final String time;
    private final String region;
    private final List<String> resources;
    private final Detail detail;

    public CodeBuildStateChangeMsg(@JsonProperty("version") String version,
                                   @JsonProperty("id") String id,
                                   @JsonProperty("detail-type") String detailType,
                                   @JsonProperty("source") String source,
                                   @JsonProperty("account") String account,
                                   @JsonProperty("time") String time,
                                   @JsonProperty("region") String region,
                                   @JsonProperty("resources") List<String> resources,
                                   @JsonProperty("detail") Detail detail) {
        this.version = version;
        this.id = id;
        this.detailType = detailType;
        this.source = source;
        this.account = account;
        this.time = time;
        this.region = region;
        this.resources = resources;
        this.detail = detail;
    }

    public Detail getDetail() {
        return detail;
    }

    public String getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

    public String getDetailType() {
        return detailType;
    }

    public String getSource() {
        return source;
    }

    public String getAccount() {
        return account;
    }

    public String getTime() {
        return time;
    }

    public String getRegion() {
        return region;
    }

    public List<String> getResources() {
        return resources;
    }
}
