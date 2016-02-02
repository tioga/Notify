package org.tiogasolutions.notify.kernel.config;

public class CouchServersConfig {

  private String masterUrl;
  private String masterUsername;
  private String masterPassword;
  private String masterDatabaseName;

  private String notificationUrl;
  private String notificationUsername;
  private String notificationPassword;
  private String notificationDatabasePrefix;
  private String notificationDatabaseSuffix;

  private String requestUrl;
  private String requestUsername;
  private String requestPassword;
  private String requestDatabasePrefix;
  private String requestDatabaseSuffix;

  public CouchServersConfig() {
  }

  public CouchServersConfig(CouchServersConfig couch) {
    setMasterUrl(couch.getMasterUrl());
    setMasterUsername(couch.getMasterUsername());
    setMasterPassword(couch.getMasterPassword());
    setMasterDatabaseName(couch.getMasterDatabaseName());

    setNotificationUrl(couch.getNotificationUrl());
    setNotificationUsername(couch.getNotificationUsername());
    setNotificationPassword(couch.getNotificationPassword());
    setNotificationDatabasePrefix(couch.getNotificationDatabasePrefix());
    setNotificationDatabaseSuffix(couch.getNotificationDatabaseSuffix());

    setRequestUrl(couch.getRequestUrl());
    setRequestUsername(couch.getRequestUsername());
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

  public String getMasterUsername() {
    return masterUsername;
  }

  public void setMasterUsername(String masterUsername) {
    this.masterUsername = masterUsername;
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

  public String getNotificationUsername() {
    return notificationUsername;
  }

  public void setNotificationUsername(String notificationUsername) {
    this.notificationUsername = notificationUsername;
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

  public String getRequestUsername() {
    return requestUsername;
  }

  public void setRequestUsername(String requestUsername) {
    this.requestUsername = requestUsername;
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
    if (masterUsername != null ? !masterUsername.equals(that.masterUsername) : that.masterUsername != null)
      return false;
    if (notificationDatabasePrefix != null ? !notificationDatabasePrefix.equals(that.notificationDatabasePrefix) : that.notificationDatabasePrefix != null)
      return false;
    if (notificationDatabaseSuffix != null ? !notificationDatabaseSuffix.equals(that.notificationDatabaseSuffix) : that.notificationDatabaseSuffix != null)
      return false;
    if (notificationPassword != null ? !notificationPassword.equals(that.notificationPassword) : that.notificationPassword != null)
      return false;
    if (notificationUrl != null ? !notificationUrl.equals(that.notificationUrl) : that.notificationUrl != null)
      return false;
    if (notificationUsername != null ? !notificationUsername.equals(that.notificationUsername) : that.notificationUsername != null)
      return false;
    if (requestDatabasePrefix != null ? !requestDatabasePrefix.equals(that.requestDatabasePrefix) : that.requestDatabasePrefix != null)
      return false;
    if (requestDatabaseSuffix != null ? !requestDatabaseSuffix.equals(that.requestDatabaseSuffix) : that.requestDatabaseSuffix != null)
      return false;
    if (requestPassword != null ? !requestPassword.equals(that.requestPassword) : that.requestPassword != null)
      return false;
    if (requestUrl != null ? !requestUrl.equals(that.requestUrl) : that.requestUrl != null) return false;
    if (requestUsername != null ? !requestUsername.equals(that.requestUsername) : that.requestUsername != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = masterUrl != null ? masterUrl.hashCode() : 0;
    result = 31 * result + (masterUsername != null ? masterUsername.hashCode() : 0);
    result = 31 * result + (masterPassword != null ? masterPassword.hashCode() : 0);
    result = 31 * result + (masterDatabaseName != null ? masterDatabaseName.hashCode() : 0);
    result = 31 * result + (notificationUrl != null ? notificationUrl.hashCode() : 0);
    result = 31 * result + (notificationUsername != null ? notificationUsername.hashCode() : 0);
    result = 31 * result + (notificationPassword != null ? notificationPassword.hashCode() : 0);
    result = 31 * result + (notificationDatabasePrefix != null ? notificationDatabasePrefix.hashCode() : 0);
    result = 31 * result + (notificationDatabaseSuffix != null ? notificationDatabaseSuffix.hashCode() : 0);
    result = 31 * result + (requestUrl != null ? requestUrl.hashCode() : 0);
    result = 31 * result + (requestUsername != null ? requestUsername.hashCode() : 0);
    result = 31 * result + (requestPassword != null ? requestPassword.hashCode() : 0);
    result = 31 * result + (requestDatabasePrefix != null ? requestDatabasePrefix.hashCode() : 0);
    result = 31 * result + (requestDatabaseSuffix != null ? requestDatabaseSuffix.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "CouchServersConfig{" +
        "masterUrl='" + masterUrl + '\'' +
        ", masterUserName='" + masterUsername + '\'' +
        ", masterPassword='" + masterPassword + '\'' +
        ", masterDatabaseName='" + masterDatabaseName + '\'' +
        ", notificationUrl='" + notificationUrl + '\'' +
        ", notificationUsername='" + notificationUsername + '\'' +
        ", notificationPassword='" + notificationPassword + '\'' +
        ", notificationDatabasePrefix='" + notificationDatabasePrefix + '\'' +
        ", notificationDatabaseSuffix='" + notificationDatabaseSuffix + '\'' +
        ", requestUrl='" + requestUrl + '\'' +
        ", requestUsername='" + requestUsername + '\'' +
        ", requestPassword='" + requestPassword + '\'' +
        ", requestDatabasePrefix='" + requestDatabasePrefix + '\'' +
        ", requestDatabaseSuffix='" + requestDatabaseSuffix + '\'' +
        '}';
  }
}
