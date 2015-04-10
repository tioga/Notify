package org.tiogasolutions.notify.pub;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

public class DomainProfile {

  private String profileId;
  private String revision;
  private String domainName;
  private DomainStatus domainStatus;
  private String apiKey;
  private String apiPassword;
  private String notificationDbName;
  private String requestDbName;
  private RouteCatalog routeCatalog;

  public DomainProfile(@JsonProperty("profileId") String profileId,
                       @JsonProperty("revision") String revision,
                       @JsonProperty("domainName") String domainName,
                       @JsonProperty("domainStatus") DomainStatus domainStatus,
                       @JsonProperty("apiKey") String apiKey,
                       @JsonProperty("apiPassword") String apiPassword,
                       @JsonProperty("notificationDbName") String notificationDbName,
                       @JsonProperty("requestDbName") String requestDbName,
                       @JsonProperty("routeCatalog") RouteCatalog routeCatalog) {

    this.profileId = profileId;
    this.revision = revision;
    this.domainName = domainName;
    this.domainStatus = domainStatus;
    this.apiKey = apiKey;
    this.apiPassword = apiPassword;
    this.notificationDbName = notificationDbName;
    this.requestDbName = requestDbName;
    this.routeCatalog = routeCatalog;
  }

  public String getProfileId() {
    return profileId;
  }

  public final String getRevision() {
    return revision;
  }

  public String getDomainName() {
    return domainName;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getApiPassword() {
    return apiPassword;
  }

  public String getNotificationDbName() {
    return notificationDbName;
  }

  public String getRequestDbName() {
    return requestDbName;
  }

  public DomainStatus getDomainStatus() {
    return domainStatus;
  }

  public RouteCatalog getRouteCatalog() {
    return routeCatalog;
  }
}
