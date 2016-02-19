package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Event implements Comparable<Event> {

  private final String type;

  private final String actor;
  private final String actorType;
  private final String actorName;

  private final String actee;
  private final String acteeType;
  private final String acteeName;

  private final ZonedDateTime timestamp;
  private final Metadata metadata;
  private final String spaceGuid;
  private final String organizationGuid;

  public Event(@JsonProperty("type") String type,
               @JsonProperty("actor") String actor,
               @JsonProperty("actor_type") String actorType,
               @JsonProperty("actor_name") String actorName,
               @JsonProperty("actee") String actee,
               @JsonProperty("actee_type") String acteeType,
               @JsonProperty("actee_name") String acteeName,
               @JsonProperty("timestamp") ZonedDateTime timestamp,
               @JsonProperty("metadata") Metadata metadata,
               @JsonProperty("space_guid") String spaceGuid,
               @JsonProperty("organization_guid") String organizationGuid) {

    this.type = type;
    this.actor = actor;
    this.actorType = actorType;
    this.actorName = actorName;
    this.actee = actee;
    this.acteeType = acteeType;
    this.acteeName = acteeName;
    this.timestamp = timestamp;
    this.metadata = metadata;
    this.spaceGuid = spaceGuid;
    this.organizationGuid = organizationGuid;
  }

  public String getType() {
    return type;
  }

  public String getActor() {
    return actor;
  }

  public String getActorType() {
    return actorType;
  }

  public String getActorName() {
    return actorName;
  }

  public String getActee() {
    return actee;
  }

  public String getActeeType() {
    return acteeType;
  }

  public String getActeeName() {
    return acteeName;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public String getSpaceGuid() {
    return spaceGuid;
  }

  public String getOrganizationGuid() {
    return organizationGuid;
  }

  @Override
  public int compareTo(Event that) {
    int diff = this.timestamp.compareTo(that.timestamp);
    if (diff != 0) return diff;

    diff = this.type.compareTo(that.type);
    if (diff != 0) return diff;

    diff = this.actee.compareTo(that.actee);
    if (diff != 0) return diff;

    diff = this.actor.compareTo(that.actor);
    return diff;
  }
}
