package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventResource implements Comparable<EventResource> {

  private final Event event;
  private final Metadata metadata;

  public EventResource(@JsonProperty("entity") Event event,
                       @JsonProperty("metadata") Metadata metadata) {

    this.event = event;
    this.metadata = metadata;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public Event getEvent() {
    return event;
  }

  @Override
  public int compareTo(EventResource that) {
    return this.getEvent().compareTo(that.getEvent());
  }
}
