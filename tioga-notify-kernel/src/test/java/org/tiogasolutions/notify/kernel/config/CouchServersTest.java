package org.tiogasolutions.notify.kernel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.kernel.domain.DomainProfileEntity;
import org.tiogasolutions.notify.kernel.domain.DomainStore;
import org.tiogasolutions.notify.test.AbstractSpringTest;

import static org.testng.Assert.assertEquals;

@Test
public class CouchServersTest extends AbstractSpringTest {
  private final String DOMAIN_NAME = "dn1";

  @Autowired
  private CouchEnvironment couchEnvironment;

  @Autowired
  private CouchServersConfig couchServersConfig;

  @Autowired
  private CouchServers couchServers;

  @BeforeClass
  public void beforeClass() {
    // Delete the test notification databases.
    couchServers.deleteDomainDatabases(DOMAIN_NAME);
  }

  public void buildDBName() {

    assertEquals(couchServers.buildDbName(DOMAIN_NAME, "default", "pre", null), "pre" + DOMAIN_NAME);
    assertEquals(couchServers.buildDbName(DOMAIN_NAME, "default", null, "suffix"), DOMAIN_NAME + "suffix");
    assertEquals(couchServers.buildDbName(DOMAIN_NAME, "default", "pre", "suffix"), "pre" + DOMAIN_NAME + "suffix");

    assertEquals(couchServers.buildDbName(DOMAIN_NAME, "default", null, null), "default" + DOMAIN_NAME);
    assertEquals(couchServers.buildDbName(DOMAIN_NAME, "default", "", ""), "default" + DOMAIN_NAME);

  }

  public void buildRequestDbName() {
    CouchServersConfig config = new CouchServersConfig(couchServersConfig);
    config.setRequestDatabasePrefix("pre");
    config.setRequestDatabaseSuffix(null);
    CouchServers couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildRequestDbName(DOMAIN_NAME), "pre" + DOMAIN_NAME);

    config.setRequestDatabasePrefix(null);
    config.setRequestDatabaseSuffix("suffix");
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildRequestDbName(DOMAIN_NAME), DOMAIN_NAME + "suffix");

    config.setRequestDatabasePrefix("prefix");
    config.setRequestDatabaseSuffix("suffix");
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildRequestDbName(DOMAIN_NAME), "prefix" + DOMAIN_NAME + "suffix");

    config.setRequestDatabasePrefix("");
    config.setRequestDatabaseSuffix("");
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildRequestDbName(DOMAIN_NAME), "notify-request-" + DOMAIN_NAME);

    config.setRequestDatabasePrefix(null);
    config.setRequestDatabaseSuffix(null);
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildRequestDbName(DOMAIN_NAME), "notify-request-" + DOMAIN_NAME);

  }

  public void buildNotificationDbName() {
    CouchServersConfig config = new CouchServersConfig(couchServersConfig);
    config.setNotificationDatabasePrefix("pre");
    config.setNotificationDatabaseSuffix(null);
    CouchServers couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildNotificationDbName(DOMAIN_NAME), "pre" + DOMAIN_NAME);

    config.setNotificationDatabasePrefix(null);
    config.setNotificationDatabaseSuffix("suffix");
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildNotificationDbName(DOMAIN_NAME), DOMAIN_NAME + "suffix");

    config.setNotificationDatabasePrefix("prefix");
    config.setNotificationDatabaseSuffix("suffix");
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildNotificationDbName(DOMAIN_NAME), "prefix" + DOMAIN_NAME + "suffix");

    config.setNotificationDatabasePrefix("");
    config.setNotificationDatabaseSuffix("");
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildNotificationDbName(DOMAIN_NAME), "notify-notification-" + DOMAIN_NAME);

    config.setNotificationDatabasePrefix(null);
    config.setNotificationDatabaseSuffix(null);
    couchServers = new CouchServers(couchEnvironment, config);
    assertEquals(couchServers.buildNotificationDbName(DOMAIN_NAME), "notify-notification-" + DOMAIN_NAME);

  }


}