package org.tiogasolutions.notify.sender.lambda.build;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Detail {
    private final String buildStatus;
    private final String projectName;
    private final String buildId;
    private final String currentPhase;
    private final String currentPhaseContext;
    private final String version;

    public Detail(@JsonProperty("build-status") String buildStatus,
                  @JsonProperty("project-name") String projectName,
                  @JsonProperty("build-id") String buildId,
                  @JsonProperty("current-phase") String currentPhase,
                  @JsonProperty("current-phase-context") String currentPhaseContext,
                  @JsonProperty("version") String version) {

        this.buildStatus = buildStatus;
        this.projectName = projectName;
        this.buildId = buildId;
        this.currentPhase = currentPhase;
        this.currentPhaseContext = currentPhaseContext;
        this.version = version;
    }

    public String getBuildStatus() {
        return buildStatus;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getBuildId() {
        return buildId;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public String getCurrentPhaseContext() {
        return currentPhaseContext;
    }

    public String getVersion() {
        return version;
    }
}
