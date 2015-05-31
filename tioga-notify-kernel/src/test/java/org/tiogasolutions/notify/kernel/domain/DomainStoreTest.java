package org.tiogasolutions.notify.kernel.domain;

import org.springframework.mock.env.MockEnvironment;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.kernel.KernelAbstractTest;
import org.tiogasolutions.notify.kernel.config.CouchServers;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

@Test
public class DomainStoreTest extends KernelAbstractTest {
  private final String DOMAIN_NAME = "dn1";
  private final String API_KEY = "api-key";
  private final String API_PASSWORD = "api-password";

  @Inject
  private CouchServersConfig couchServersConfig;
  @Inject
  private CouchServers couchServers;

  @BeforeClass
  public void setup() {
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

  public void createAndDeleteDomain() {

    DomainStore domainStore = new DomainStore(couchServers);
    DomainProfileEntity profileEntity = null;
    try {
      // Create the domain
      profileEntity = domainStore.createDomain(DOMAIN_NAME, API_KEY, API_PASSWORD);

      // Verify basics
      assertEquals(profileEntity.getApiKey(), API_KEY);
      assertEquals(profileEntity.getApiPassword(), API_PASSWORD);
      assertEquals(profileEntity.getDomainName(), DOMAIN_NAME);

      // Verify notification and request db names are correct.
      assertEquals(profileEntity.getNotificationDbName(), "test-notify-" + DOMAIN_NAME);
      assertEquals(profileEntity.getRequestDbName(), "test-notify-request-" + DOMAIN_NAME);

    } finally {
      if (profileEntity != null) {
        domainStore.deleteDomain(profileEntity);
      }
    }
  }

  @Test(dependsOnMethods = "createAndDeleteDomain")
  public void createDomainWithSuffix() {

    CouchServersConfig localConfig = new CouchServersConfig(couchServersConfig);
    localConfig.setNotificationDatabasePrefix(null);
    localConfig.setNotificationDatabaseSuffix("-not");
    localConfig.setRequestDatabasePrefix(null);
    localConfig.setRequestDatabaseSuffix("-not-req");
    MockEnvironment environment = new MockEnvironment();
    environment.setActiveProfiles("test");
    CouchServers couchServers = new CouchServers(environment, localConfig);
    DomainStore domainStore = new DomainStore(couchServers);

    // Delete databases just in case they still exist.
    couchServers.deleteDomainDatabases(DOMAIN_NAME);

    // Create the domain
    DomainProfileEntity profileEntity = domainStore.createDomain(DOMAIN_NAME, API_KEY, API_PASSWORD);
    try {

      // Verify basics
      assertEquals(profileEntity.getApiKey(), API_KEY);
      assertEquals(profileEntity.getApiPassword(), API_PASSWORD);
      assertEquals(profileEntity.getDomainName(), DOMAIN_NAME);

      // Verify notification and request db names are correct.
      assertEquals(profileEntity.getNotificationDbName(), DOMAIN_NAME + "-not");
      assertEquals(profileEntity.getRequestDbName(), DOMAIN_NAME + "-not-req");

    } finally {
      domainStore.deleteDomain(profileEntity);
    }
  }


}