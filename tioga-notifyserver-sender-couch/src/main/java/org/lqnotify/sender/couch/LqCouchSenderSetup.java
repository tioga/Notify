package org.lqnotify.sender.couch;

public class LqCouchSenderSetup {

  private final String couchUrl;
  private final String databaseName;
  private final String username;
  private final String password;

  public LqCouchSenderSetup(String couchUrl, String databaseName, String username, String password) {
    this.couchUrl = couchUrl;
    this.databaseName = databaseName;
    this.username = username;
    this.password = password;
  }

  public String getCouchUrl() {
    return couchUrl;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
