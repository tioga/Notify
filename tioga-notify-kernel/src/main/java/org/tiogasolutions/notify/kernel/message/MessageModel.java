package org.tiogasolutions.notify.kernel.message;

import org.tiogasolutions.notify.pub.*;
import org.tiogasolutions.notify.pub.route.ArgValueMap;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
* This class is used by the processors in conjunction with
 * Thymeleaf to generate messages (ie emails) for the end users.
*/
public class MessageModel {
  
  private final String destinationName;
  private final ArgValueMap destinationMap;

  private final String self;
  private final String notificationId;
  private final String revision;
  private final String topic;
  private final String summary;
  private final String trackingId;
  private final ZonedDateTime createdAt;
  private final Map<String,String> traitMap;
  private final List<AttachmentInfo> attachmentInfoList;
  private final ExceptionInfo exceptionInfo;

  private final String profileId;
  private final String domainName;
  private final DomainStatus domainStatus;
  private final String apiKey;
  private final String apiPassword;
  private final String notificationDbName;
  private final String requestDbName;
  private final RouteCatalog routeCatalog;

  public MessageModel(DomainProfile domainProfile, Notification notification, Task task) {

    destinationName = task.getDestination().getName();
    destinationMap = task.getDestination().getArgValueMap();

    self = (notification.getSelf() == null) ? null : notification.getSelf().toASCIIString();
    notificationId = notification.getNotificationId();
    revision = notification.getRevision();
    topic = notification.getTopic();
    summary = notification.getSummary();
    trackingId = notification.getTrackingId();
    createdAt = notification.getCreatedAt();
    attachmentInfoList = notification.getAttachmentInfoList();
    exceptionInfo = notification.getExceptionInfo();
    traitMap = notification.getTraitMap();


    profileId = domainProfile.getProfileId();
    domainName = domainProfile.getDomainName();
    domainStatus = domainProfile.getDomainStatus();
    apiKey = domainProfile.getApiKey();
    apiPassword = domainProfile.getApiPassword();
    notificationDbName = domainProfile.getNotificationDbName();
    requestDbName = domainProfile.getRequestDbName();
    routeCatalog = domainProfile.getRouteCatalog();
  }

  public String getDestinationName() {
    return destinationName;
  }

  public ArgValueMap getDestinationMap() {
    return destinationMap;
  }

  public String getSelf() {
    return self;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public String getRevision() {
    return revision;
  }

  public String getTopic() {
    return topic;
  }

  public String getSummary() {
    return summary;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public ZonedDateTime getCreatedAt() {
    return createdAt;
  }

  public List<AttachmentInfo> getAttachmentInfoList() {
    return attachmentInfoList;
  }

  public ExceptionInfo getExceptionInfo() {
    return exceptionInfo;
  }

  public Map<String, String> getTraitMap() {
    return traitMap;
  }

  public static class Trait {
    private final String key;
    private final String value;
    public Trait(String key, String value) {
      this.key = key;
      this.value = value;
    }
    public String getKey() { return key; }
    public String getValue() { return value; }
  }

  public String getProfileId() {
    return profileId;
  }

  public String getDomainName() {
    return domainName;
  }

  public DomainStatus getDomainStatus() {
    return domainStatus;
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

  public RouteCatalog getRouteCatalog() {
    return routeCatalog;
  }
}
