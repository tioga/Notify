package org.tiogasolutions.notify.sender.lambda.pipeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CodePipelineJob {

    private final Job job;

    public CodePipelineJob(@JsonProperty("CodePipelineJob") Job job) {
        this.job = job;
    }

    @JsonIgnore
    public String getUserParameters() {
        // "{ \"topic\": \"Pipeline Status\",   \"summary\": \"The deployment of notify-server-production has been approved.\" }";
        return  job.data.actionConfiguration.configuration.userParameters;
    }

    public Job getJob() {
        return job;
    }

    public String toString() {
        return String.format("%s { job=%s }", getClass().getSimpleName(), job);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Job {
        private final String id;
        private final String accountId;
        private final Data data;

        public Job(@JsonProperty("id") String id,
                   @JsonProperty("accountId") String accountId,
                   @JsonProperty("data") Data data) {
            this.id = id;
            this.accountId = accountId;
            this.data = data;
        }

        public String getId() {
            return id;
        }

        public String getAccountId() {
            return accountId;
        }

        public Data getData() {
            return data;
        }

        public String toString() {
            return String.format("%s {id=%s, accountId=%s}", getClass().getSimpleName(), id, accountId);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        private final ActionConfiguration actionConfiguration;

        public Data(@JsonProperty("actionConfiguration") ActionConfiguration actionConfiguration) {
            this.actionConfiguration = actionConfiguration;
        }

        public ActionConfiguration getActionConfiguration() {
            return actionConfiguration;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ActionConfiguration {

        private final Configuration configuration;

        public ActionConfiguration(@JsonProperty("configuration") Configuration configuration) {
            this.configuration = configuration;
        }

        public Configuration getConfiguration() {
            return configuration;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Configuration {

        private final String functionName;
        private final String userParameters;

        public Configuration(@JsonProperty("FunctionName") String functionName,
                             @JsonProperty("UserParameters") String userParameters) {
            this.functionName = functionName;
            this.userParameters = userParameters;
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getUserParameters() {
            return userParameters;
        }
    }

    public static class Response {
        private final String continuationToken;
        private final String jobId;
        private final CurrentRevision currentRevision;
        private final ExecutionDetails executionDetails;

        public Response(@JsonProperty("jobId") String jobId) {

            this.continuationToken = null;
            this.jobId = jobId;
            this.currentRevision = null;
            this.executionDetails = null;
        }

        public String getContinuationToken() {
            return continuationToken;
        }

        public String getJobId() {
            return jobId;
        }

        public CurrentRevision getCurrentRevision() {
            return currentRevision;
        }

        public ExecutionDetails getExecutionDetails() {
            return executionDetails;
        }
    }

    public static class CurrentRevision {
        private final String changeIdentifier;
        private final int created;
        private final String revision;
        private final String revisionSummary;

        public CurrentRevision(@JsonProperty("changeIdentifier") String changeIdentifier,
                               @JsonProperty("created") int created,
                               @JsonProperty("revision") String revision,
                               @JsonProperty("revisionSummary") String revisionSummary) {

            this.changeIdentifier = changeIdentifier;
            this.created = created;
            this.revision = revision;
            this.revisionSummary = revisionSummary;
        }

        public String getChangeIdentifier() {
            return changeIdentifier;
        }

        public int getCreated() {
            return created;
        }

        public String getRevision() {
            return revision;
        }

        public String getRevisionSummary() {
            return revisionSummary;
        }
    }

    public static class ExecutionDetails {
        private final String externalExecutionId;
        private final int percentComplete;
        private final String summary;

        public ExecutionDetails(@JsonProperty("externalExecutionId") String externalExecutionId,
                                @JsonProperty("percentComplete") int percentComplete,
                                @JsonProperty("summary") String summary) {

            this.externalExecutionId = externalExecutionId;
            this.percentComplete = percentComplete;
            this.summary = summary;
        }

        public String getExternalExecutionId() {
            return externalExecutionId;
        }

        public int getPercentComplete() {
            return percentComplete;
        }

        public String getSummary() {
            return summary;
        }
    }
}
