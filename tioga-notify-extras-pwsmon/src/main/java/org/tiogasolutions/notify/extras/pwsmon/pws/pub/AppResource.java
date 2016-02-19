package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AppResource implements Comparable<AppResource> {

  private final App app;
  private final Metadata metadata;

  public AppResource(@JsonProperty("entity") App app,
                     @JsonProperty("metadata") Metadata metadata) {

    this.app = app;
    this.metadata = metadata;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  public App getApp() {
    return app;
  }

  @Override
  public int compareTo(AppResource that) {
    return this.getMetadata().compareTo(that.getMetadata());
  }
}
