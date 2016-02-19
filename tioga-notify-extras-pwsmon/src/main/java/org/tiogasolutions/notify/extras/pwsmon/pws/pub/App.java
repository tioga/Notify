package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Map;

@SuppressWarnings({"unused"})
public class App {

  private final String name;
  private final boolean production;
  private final String spaceGuid;
  private final String stackGuid;
  private final String buildpack;
  private final String detectedBuildpack;
  private final Map<String,String> environment;
  private final int memory;
  private final int instances;
  private final int diskQuota;
  private final String state;
  private final String version;
  private final String command;
  private final boolean console;
  private final String debug;
  private final String stagingTaskId;
  private final String packageState;
  private final String healthCheckType;
  private final String healthCheckTimeout;
  private final String stagingFailedReason;
  private final String stagingFailedDescription;
  private final boolean diego;
  private final String dockerImage;
  private final ZonedDateTime packageUpdatedAt;
  private final String detectedStartCommand;
  private final boolean enableSsh;
  private final Map<String,String> dockerCredentials;
  private final int[] ports;
  private final String spaceUrl;
  private final String stackUrl;
  private final String eventsUrl;
  private final String serviceBindingsUrl;
  private final String routesUrl;

  public App(@JsonProperty("name") String name,
             @JsonProperty("production") boolean production,
             @JsonProperty("space_guid") String spaceGuid,
             @JsonProperty("stack_guid") String stackGuid,
             @JsonProperty("buildpack") String buildpack,
             @JsonProperty("detected_buildpack") String detectedBuildpack,
             @JsonProperty("environment_json") Map<String,String> environment,
             @JsonProperty("memory") int memory,
             @JsonProperty("instances") int instances,
             @JsonProperty("disk_quota") int diskQuota,
             @JsonProperty("state") String state,
             @JsonProperty("version") String version,
             @JsonProperty("command") String command,
             @JsonProperty("console") boolean console,
             @JsonProperty("debug") String debug,
             @JsonProperty("staging_task_id") String stagingTaskId,
             @JsonProperty("package_state") String packageState,
             @JsonProperty("health_check_type") String healthCheckType,
             @JsonProperty("health_check_timeout") String healthCheckTimeout,
             @JsonProperty("staging_failed_reason") String stagingFailedReason,
             @JsonProperty("staging_failed_description") String stagingFailedDescription,
             @JsonProperty("diego") boolean diego,
             @JsonProperty("docker_image") String dockerImage,
             @JsonProperty("package_updated_at") ZonedDateTime packageUpdatedAt,
             @JsonProperty("detected_start_command") String detectedStartCommand,
             @JsonProperty("enable_ssh") boolean enableSsh,
             @JsonProperty("docker_credentials_json") Map<String,String> dockerCredentials,
             @JsonProperty("ports") int[] ports,
             @JsonProperty("space_url") String spaceUrl,
             @JsonProperty("stack_url") String stackUrl,
             @JsonProperty("events_url") String eventsUrl,
             @JsonProperty("service_bindings_url") String serviceBindingsUrl,
             @JsonProperty("routes_url") String routesUrl) {

    this.name = name;
    this.production = production;
    this.spaceGuid = spaceGuid;
    this.stackGuid = stackGuid;
    this.buildpack = buildpack;
    this.detectedBuildpack = detectedBuildpack;
    this.environment = environment;
    this.memory = memory;
    this.instances = instances;
    this.diskQuota = diskQuota;
    this.state = state;
    this.version = version;
    this.command = command;
    this.console = console;
    this.debug = debug;
    this.stagingTaskId = stagingTaskId;
    this.packageState = packageState;
    this.healthCheckType = healthCheckType;
    this.healthCheckTimeout = healthCheckTimeout;
    this.stagingFailedReason = stagingFailedReason;
    this.stagingFailedDescription = stagingFailedDescription;
    this.diego = diego;
    this.dockerImage = dockerImage;
    this.packageUpdatedAt = packageUpdatedAt;
    this.detectedStartCommand = detectedStartCommand;
    this.enableSsh = enableSsh;
    this.dockerCredentials = dockerCredentials;
    this.ports = ports;
    this.spaceUrl = spaceUrl;
    this.stackUrl = stackUrl;
    this.eventsUrl = eventsUrl;
    this.serviceBindingsUrl = serviceBindingsUrl;
    this.routesUrl = routesUrl;
  }

  public String getName() {
    return name;
  }

  public boolean isProduction() {
    return production;
  }

  public String getSpaceGuid() {
    return spaceGuid;
  }

  public String getStackGuid() {
    return stackGuid;
  }

  public String getBuildpack() {
    return buildpack;
  }

  public String getDetectedBuildpack() {
    return detectedBuildpack;
  }

  public Map<String,String> getEnvironment() {
    return environment;
  }

  public int getMemory() {
    return memory;
  }

  public int getInstances() {
    return instances;
  }

  public int getDiskQuota() {
    return diskQuota;
  }

  public String getState() {
    return state;
  }

  public String getVersion() {
    return version;
  }

  public String getCommand() {
    return command;
  }

  public boolean isConsole() {
    return console;
  }

  public String getDebug() {
    return debug;
  }

  public String getStagingTaskId() {
    return stagingTaskId;
  }

  public String getPackageState() {
    return packageState;
  }

  public String getHealthCheckType() {
    return healthCheckType;
  }

  public String getHealthCheckTimeout() {
    return healthCheckTimeout;
  }

  public String getStagingFailedReason() {
    return stagingFailedReason;
  }

  public String getStagingFailedDescription() {
    return stagingFailedDescription;
  }

  public boolean isDiego() {
    return diego;
  }

  public String getDockerImage() {
    return dockerImage;
  }

  public ZonedDateTime getPackageUpdatedAt() {
    return packageUpdatedAt;
  }

  public String getDetectedStartCommand() {
    return detectedStartCommand;
  }

  public boolean isEnableSsh() {
    return enableSsh;
  }

  public Map<String,String> getDockerCredentials() {
    return dockerCredentials;
  }

  public int[] getPorts() {
    return ports;
  }

  public String getSpaceUrl() {
    return spaceUrl;
  }

  public String getStackUrl() {
    return stackUrl;
  }

  public String getEventsUrl() {
    return eventsUrl;
  }

  public String getServiceBindingsUrl() {
    return serviceBindingsUrl;
  }

  public String getRoutesUrl() {
    return routesUrl;
  }
}
