package org.lqnotify.kernel.domain;

import org.tiogasolutions.couchace.annotations.CouchEntity;
import org.tiogasolutions.couchace.annotations.CouchId;
import org.tiogasolutions.couchace.annotations.CouchRevision;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.lqnotify.pub.DomainProfile;
import org.lqnotify.pub.DomainStatus;
import org.lqnotify.pub.route.RouteCatalog;

@CouchEntity("DomainProfile")
public class DomainProfileEntity {

  private final String profileId;
  private final String revision;
  private final String domainName;
  private DomainStatus domainStatus;
  private String apiKey;
  private String apiPassword;
  private final String notificationDbName;
  private final String requestDbName;
  private RouteCatalog routeCatalog;

  public DomainProfileEntity(@JsonProperty("profileId") String profileId,
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

  public DomainProfile toModel() {
    return new DomainProfile(profileId,
        revision,
        domainName,
        domainStatus,
        apiKey,
        apiPassword,
        notificationDbName,
        requestDbName,
        routeCatalog);
  }

  @CouchId
  public String getProfileId() {
    return profileId;
  }

  @CouchRevision
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

  public void setDomainStatus(DomainStatus domainStatus) {
    this.domainStatus = domainStatus;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public void setApiPassword(String apiPassword) {
    this.apiPassword = apiPassword;
  }

  public RouteCatalog getRouteCatalog() {
    return routeCatalog;
  }

  public void setRouteCatalog(RouteCatalog routeCatalog) {
    this.routeCatalog = routeCatalog;
  }

  public static DomainProfileEntity newEntity(String domainName,
                                              String apiKey,
                                              String apiPassword,
                                              String notificationDbName,
                                              String requestDbName,
                                              RouteCatalog routeCatalog) {
    return new DomainProfileEntity(
      TimeUuid.randomUUID().toString(),
      null,
      domainName,
      DomainStatus.ACTIVE,
      apiKey,
      apiPassword,
      notificationDbName,
      requestDbName,
      routeCatalog);
  }
}
