package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class GetAppsResponse {

  private final int totalResults;
  private final int totalPages;
  private final String prevUrl;
  private final String nextUrl;
  private final List<AppResource> appResources;

  public GetAppsResponse(@JsonProperty("total_results") int totalResults,
                         @JsonProperty("total_pages") int totalPages,
                         @JsonProperty("prev_url") String prevUrl,
                         @JsonProperty("next_url") String nextUrl,
                         @JsonProperty("resources") ArrayList<AppResource> appResources) {

    this.totalResults = totalResults;
    this.totalPages = totalPages;
    this.prevUrl = prevUrl;
    this.nextUrl = nextUrl;
    this.appResources = appResources;
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

  public List<AppResource> getAppResources() {
    return appResources;
  }

  public List<App> getApplications() {
    List<App> apps = new ArrayList<>();

    for (AppResource resource : appResources) {
      App app = resource.getApp();
      apps.add(app);
    }

    return apps;
  }
}
