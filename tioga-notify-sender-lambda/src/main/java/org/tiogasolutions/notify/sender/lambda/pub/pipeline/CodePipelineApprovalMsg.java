package org.tiogasolutions.notify.sender.lambda.pub.pipeline;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CodePipelineApprovalMsg {

    private final String region;
    private final String consoleLink;
    private final Approval approval;

    public CodePipelineApprovalMsg(@JsonProperty("region") String region,
                                   @JsonProperty("consoleLink") String consoleLink,
                                   @JsonProperty("approval") Approval approval) {

        this.region = region;
        this.consoleLink = consoleLink;
        this.approval = approval;
    }

    public String getRegion() {
        return region;
    }

    public String getConsoleLink() {
        return consoleLink;
    }

    public Approval getApproval() {
        return approval;
    }
}
