package org.tiogasolutions.notify.kernel.config;

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

  public CouchServersConfig() {
  }

  public CouchServersConfig(CouchServersConfig couch) {
    setMasterUrl(couch.getMasterUrl());
    setMasterUserName(couch.getMasterUserName());
    setMasterPassword(couch.getMasterPassword());
    setMasterDatabaseName(couch.getMasterDatabaseName());

    setNotificationUrl(couch.getNotificationUrl());
    setNotificationUserName(couch.getNotificationUserName());
    setNotificationPassword(couch.getNotificationPassword());
    setNotificationDatabasePrefix(couch.getNotificationDatabasePrefix());
    setNotificationDatabaseSuffix(couch.getNotificationDatabaseSuffix());

    setRequestUrl(couch.getRequestUrl());
    setRequestUserName(couch.getRequestUserName());
    setRequestPassword(couch.getRequestPassword());
    setRequestDatabasePrefix(couch.getRequestDatabasePrefix());
    setRequestDatabaseSuffix(couch.getRequestDatabaseSuffix());
  }


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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CouchServersConfig that = (CouchServersConfig) o;

    if (masterDatabaseName != null ? !masterDatabaseName.equals(that.masterDatabaseName) : that.masterDatabaseName != null)
      return false;
    if (masterPassword != null ? !masterPassword.equals(that.masterPassword) : that.masterPassword != null)
      return false;
    if (masterUrl != null ? !masterUrl.equals(that.masterUrl) : that.masterUrl != null) return false;
    if (masterUserName != null ? !masterUserName.equals(that.masterUserName) : that.masterUserName != null)
      return false;
    if (notificationDatabasePrefix != null ? !notificationDatabasePrefix.equals(that.notificationDatabasePrefix) : that.notificationDatabasePrefix != null)
      return false;
    if (notificationDatabaseSuffix != null ? !notificationDatabaseSuffix.equals(that.notificationDatabaseSuffix) : that.notificationDatabaseSuffix != null)
      return false;
    if (notificationPassword != null ? !notificationPassword.equals(that.notificationPassword) : that.notificationPassword != null)
      return false;
    if (notificationUrl != null ? !notificationUrl.equals(that.notificationUrl) : that.notificationUrl != null)
      return false;
    if (notificationUserName != null ? !notificationUserName.equals(that.notificationUserName) : that.notificationUserName != null)
      return false;
    if (requestDatabasePrefix != null ? !requestDatabasePrefix.equals(that.requestDatabasePrefix) : that.requestDatabasePrefix != null)
      return false;
    if (requestDatabaseSuffix != null ? !requestDatabaseSuffix.equals(that.requestDatabaseSuffix) : that.requestDatabaseSuffix != null)
      return false;
    if (requestPassword != null ? !requestPassword.equals(that.requestPassword) : that.requestPassword != null)
      return false;
    if (requestUrl != null ? !requestUrl.equals(that.requestUrl) : that.requestUrl != null) return false;
    if (requestUserName != null ? !requestUserName.equals(that.requestUserName) : that.requestUserName != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = masterUrl != null ? masterUrl.hashCode() : 0;
    result = 31 * result + (masterUserName != null ? masterUserName.hashCode() : 0);
    result = 31 * result + (masterPassword != null ? masterPassword.hashCode() : 0);
    result = 31 * result + (masterDatabaseName != null ? masterDatabaseName.hashCode() : 0);
    result = 31 * result + (notificationUrl != null ? notificationUrl.hashCode() : 0);
    result = 31 * result + (notificationUserName != null ? notificationUserName.hashCode() : 0);
    result = 31 * result + (notificationPassword != null ? notificationPassword.hashCode() : 0);
    result = 31 * result + (notificationDatabasePrefix != null ? notificationDatabasePrefix.hashCode() : 0);
    result = 31 * result + (notificationDatabaseSuffix != null ? notificationDatabaseSuffix.hashCode() : 0);
    result = 31 * result + (requestUrl != null ? requestUrl.hashCode() : 0);
    result = 31 * result + (requestUserName != null ? requestUserName.hashCode() : 0);
    result = 31 * result + (requestPassword != null ? requestPassword.hashCode() : 0);
    result = 31 * result + (requestDatabasePrefix != null ? requestDatabasePrefix.hashCode() : 0);
    result = 31 * result + (requestDatabaseSuffix != null ? requestDatabaseSuffix.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CouchServersConfig{" +
        "masterUrl='" + masterUrl + '\'' +
        ", masterUserName='" + masterUserName + '\'' +
        ", masterPassword='" + masterPassword + '\'' +
        ", masterDatabaseName='" + masterDatabaseName + '\'' +
        ", notificationUrl='" + notificationUrl + '\'' +
        ", notificationUserName='" + notificationUserName + '\'' +
        ", notificationPassword='" + notificationPassword + '\'' +
        ", notificationDatabasePrefix='" + notificationDatabasePrefix + '\'' +
        ", notificationDatabaseSuffix='" + notificationDatabaseSuffix + '\'' +
        ", requestUrl='" + requestUrl + '\'' +
        ", requestUserName='" + requestUserName + '\'' +
        ", requestPassword='" + requestPassword + '\'' +
        ", requestDatabasePrefix='" + requestDatabasePrefix + '\'' +
        ", requestDatabaseSuffix='" + requestDatabaseSuffix + '\'' +
        '}';
  }
}
