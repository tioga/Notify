package org.tiogasolutions.notify.sender.lambda.pipeline;

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

    public static class Approval {

        private final String pipelineName;
        private final String stageName;
        private final String actionName;
        private final String token;
        private final String expires;
        private final String externalEntityLink;
        private final String approvalReviewLink;
        private final String customData;

        public Approval(@JsonProperty("pipelineName") String pipelineName,
                        @JsonProperty("stageName") String stageName,
                        @JsonProperty("actionName") String actionName,
                        @JsonProperty("token") String token,
                        @JsonProperty("expires") String expires,
                        @JsonProperty("externalEntityLink") String externalEntityLink,
                        @JsonProperty("approvalReviewLink") String approvalReviewLink,
                        @JsonProperty("customData") String customData) {

            this.pipelineName = pipelineName;
            this.stageName = stageName;
            this.actionName = actionName;
            this.token = token;
            this.expires = expires;
            this.externalEntityLink = externalEntityLink;
            this.approvalReviewLink = approvalReviewLink;
            this.customData = customData;
        }

        public String getApprovalReviewLink() {
            return approvalReviewLink;
        }

        public String getPipelineName() {
            return pipelineName;
        }

        public String getStageName() {
            return stageName;
        }

        public String getActionName() {
            return actionName;
        }

        public String getToken() {
            return token;
        }

        public String getExpires() {
            return expires;
        }

        public String getExternalEntityLink() {
            return externalEntityLink;
        }

        public String getCustomData() {
            return customData;
        }
    }
}
