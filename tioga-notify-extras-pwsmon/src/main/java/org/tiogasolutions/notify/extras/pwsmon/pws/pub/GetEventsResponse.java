package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class GetEventsResponse {

  private final int totalResults;
  private final int totalPages;
  private final String prevUrl;
  private final String nextUrl;
  private final SortedSet<EventResource> eventResources;

  public GetEventsResponse(@JsonProperty("total_results") int totalResults,
                           @JsonProperty("total_pages") int totalPages,
                           @JsonProperty("prev_url") String prevUrl,
                           @JsonProperty("next_url") String nextUrl,
                           @JsonProperty("resources") TreeSet<EventResource> eventResources) {

    this.totalResults = totalResults;
    this.totalPages = totalPages;
    this.prevUrl = prevUrl;
    this.nextUrl = nextUrl;
    this.eventResources = eventResources;
  }

  public int getTotalResults() {
    return totalResults;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public String getPrevUrl() {
    return prevUrl;
  }

  public String getNextUrl() {
    return nextUrl;
  }

  public SortedSet<EventResource> getEventResources() {
    return eventResources;
  }

  public SortedSet<Event> getAppEvents(String appName) {
    SortedSet<Event> events = new TreeSet<>();

    for (EventResource resource : eventResources) {
      Event event = resource.getEvent();
      if ("app".equals(event.getActeeType()) && appName.equals(event.getActeeName())) {
        events.add(event);
      }
    }

    return events;
  }
}
