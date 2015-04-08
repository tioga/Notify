package org.lqnotify.pub;

import com.fasterxml.jackson.annotation.*;
import java.net.URI;
import java.util.*;

public class Notifications {

  private final URI next;
  private final URI prev;
  private final URI self;

  private final boolean truncated;
  private final int offset;
  private final int limit;
  private final List<Notification> notifications = new ArrayList<>();

  @JsonCreator
  public Notifications(@JsonProperty("prev") URI prev,
                       @JsonProperty("self") URI self,
                       @JsonProperty("next") URI next,

                       @JsonProperty("truncated") boolean truncated,
                       @JsonProperty("offset") int offset,
                       @JsonProperty("limit") int limit,
                       @JsonProperty("notifications") List<Notification> notifications) {

    this.next = next;
    this.prev = prev;
    this.self = self;

    this.truncated = truncated;
    this.offset = offset;
    this.limit = limit;
    this.notifications.addAll(notifications);
  }

  public URI getNext() {
    return next;
  }

  public URI getPrev() {
    return prev;
  }

  public URI getSelf() {
    return self;
  }

  public boolean isTruncated() {
    return truncated;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

  public List<Notification> getNotifications() {
    return notifications;
  }
}
