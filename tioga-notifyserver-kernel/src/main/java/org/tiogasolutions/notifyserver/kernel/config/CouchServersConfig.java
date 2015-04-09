package org.tiogasolutions.notifyserver.kernel.config;

/**
 * Created by harlan on 2/14/15.
 */
public class CouchServersConfig {
  private String masterUrl;
  private String masterUserName;
  private String masterPassword;
  private String masterDatabaseName;

  private String notificationUrl;
  private String notificationUserName;
  private String notificationPassword;
  private String notificationDatabasePrefix;
  private String notificationDatabaseSuffix;

  private String requestUrl;
  private String requestUserName;
  private String requestPassword;
  private String requestDatabasePrefix;
  private String requestDatabaseSuffix;

  public String getMasterUrl() {
    return masterUrl;
  }

  public void setMasterUrl(String masterUrl) {
    this.masterUrl = masterUrl;
  }

  public String getMasterUserName() {
    return masterUserName;
  }

  public void setMasterUserName(String masterUserName) {
    this.masterUserName = masterUserName;
  }

  public String getMasterPassword() {
    return masterPassword;
  }

  public void setMasterPassword(String masterPassword) {
    this.masterPassword = masterPassword;
  }

  public String getMasterDatabaseName() {
    return masterDatabaseName;
  }

  public void setMasterDatabaseName(String masterDatabaseName) {
    this.masterDatabaseName = masterDatabaseName;
  }

  public String getNotificationUrl() {
    return notificationUrl;
  }

  public void setNotificationUrl(String notificationUrl) {
    this.notificationUrl = notificationUrl;
  }

  public String getNotificationUserName() {
    return notificationUserName;
  }

  public void setNotificationUserName(String notificationUserName) {
    this.notificationUserName = notificationUserName;
  }

  public String getNotificationPassword() {
    return notificationPassword;
  }

  public void setNotificationPassword(String notificationPassword) {
    this.notificationPassword = notificationPassword;
  }

  public String getNotificationDatabasePrefix() {
    return notificationDatabasePrefix;
  }

  public void setNotificationDatabasePrefix(String notificationDatabasePrefix) {
    this.notificationDatabasePrefix = notificationDatabasePrefix;
  }

  public String getNotificationDatabaseSuffix() {
    return notificationDatabaseSuffix;
  }

  public void setNotificationDatabaseSuffix(String notificationDatabaseSuffix) {
    this.notificationDatabaseSuffix = notificationDatabaseSuffix;
  }

  public String getRequestUrl() {
    return requestUrl;
  }

  public void setRequestUrl(String requestUrl) {
    this.requestUrl = requestUrl;
  }

  public String getRequestUserName() {
    return requestUserName;
  }

  public void setRequestUserName(String requestUserName) {
    this.requestUserName = requestUserName;
  }

  public String getRequestPassword() {
    return requestPassword;
  }

  public void setRequestPassword(String requestPassword) {
    this.requestPassword = requestPassword;
  }

  public String getRequestDatabasePrefix() {
    return requestDatabasePrefix;
  }

  public void setRequestDatabasePrefix(String requestDatabasePrefix) {
    this.requestDatabasePrefix = requestDatabasePrefix;
  }

  public String getRequestDatabaseSuffix() {
    return requestDatabaseSuffix;
  }

  public void setRequestDatabaseSuffix(String requestDatabaseSuffix) {
    this.requestDatabaseSuffix = requestDatabaseSuffix;
  }
}
