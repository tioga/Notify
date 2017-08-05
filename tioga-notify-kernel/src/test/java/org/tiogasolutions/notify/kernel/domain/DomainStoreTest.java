package org.tiogasolutions.notify.kernel.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.kernel.config.CouchEnvironment;
import org.tiogasolutions.notify.kernel.config.CouchServers;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;
import org.tiogasolutions.notify.test.AbstractSpringTest;

import static org.testng.Assert.assertEquals;

@Test
public class DomainStoreTest extends AbstractSpringTest {
    private final String DOMAIN_NAME = "dn1";
    private final String API_KEY = "api-key";
    private final String API_PASSWORD = "api-password";

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
            assertEquals(profileEntity.getNotificationDbName(), "test-notify-" + DOMAIN_NAME + "-notification");
            assertEquals(profileEntity.getRequestDbName(), "test-notify-" + DOMAIN_NAME + "-request");

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
        CouchServers couchServers = new CouchServers(couchEnvironment, localConfig);
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