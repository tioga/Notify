package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Metadata implements Comparable<Metadata> {

  private final String guid;
  private final String url;
  private final ZonedDateTime createdAt;
  private final ZonedDateTime updatedAt;

  private final String instance;
  private final int index;
  private final String exitDescription;
  private final String reason;
  private final Request request;
  private final String routeGuid;

  public Metadata(@JsonProperty("guid") String guid,
                  @JsonProperty("url") String url,
                  @JsonProperty("created_at") ZonedDateTime createdAt,
                  @JsonProperty("updated_at") ZonedDateTime updatedAt,
                  @JsonProperty("instance") String instance,
                  @JsonProperty("index") int index,
                  @JsonProperty("exit_description") String exitDescription,
                  @JsonProperty("reason") String reason,
                  @JsonProperty("request") Request request,
                  @JsonProperty("route_guid") String routeGuid) {

    this.guid = guid;
    this.url = url;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;

    this.instance = instance;
    this.index = index;
    this.exitDescription = exitDescription;
    this.reason = reason;
    this.request = request;
    this.routeGuid = routeGuid;
  }

  public String getRouteGuid() {
    return routeGuid;
  }

  public String getGuid() {
    return guid;
  }

  public String getUrl() {
    return url;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public ZonedDateTime getUpdatedAt() {
    return updatedAt;
  }

  public String getInstance() {
    return instance;
  }

  public int getIndex() {
    return index;
  }

  public String getExitDescription() {
    return exitDescription;
  }

  public String getReason() {
    return reason;
  }

  public Request getRequest() {
    return request;
  }

  @Override
  public int compareTo(Metadata that) {
    int diff = this.getCreatedAt().compareTo(that.getCreatedAt());
    if (diff != 0) return diff;

    return this.getGuid().compareTo(that.getGuid());
  }
}
