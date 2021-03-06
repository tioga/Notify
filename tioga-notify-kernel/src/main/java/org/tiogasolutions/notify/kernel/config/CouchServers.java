package org.tiogasolutions.notify.kernel.config;

import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.couchace.core.api.CouchSetup;
import org.tiogasolutions.couchace.core.api.request.CouchFeature;
import org.tiogasolutions.couchace.core.api.request.CouchFeatureSet;
import org.tiogasolutions.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.couchace.core.internal.util.StringUtil;
import org.tiogasolutions.couchace.jackson.JacksonCouchJsonStrategy;
import org.tiogasolutions.couchace.jersey.JerseyCouchHttpClient;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.notify.NotifyJacksonModule;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CouchServers {
    private final CouchEnvironment couchEnvironment;
    private final CouchServersConfig serversConfig;

    private final CouchServer masterServer;
    private final CouchDatabase masterDatabase;

    private final CouchServer notificationServer;
    private final String notificationDatabasePrefix;
    private final String notificationDatabaseSuffix;

    private final CouchServer requestServer;
    private final String requestDatabasePrefix;
    private final String requestDatabaseSuffix;

    @Autowired
    public CouchServers(CouchEnvironment couchEnvironment, CouchServersConfig serversConfig) {
        this.couchEnvironment = couchEnvironment;
        this.serversConfig = serversConfig;

        this.notificationDatabasePrefix = serversConfig.getNotificationDatabasePrefix();
        this.notificationDatabaseSuffix = serversConfig.getNotificationDatabaseSuffix();

        this.requestDatabasePrefix = serversConfig.getRequestDatabasePrefix();
        this.requestDatabaseSuffix = serversConfig.getRequestDatabaseSuffix();

        // CouchJsonStrategy used by all.
        JacksonCouchJsonStrategy jsonStrategy = new JacksonCouchJsonStrategy(
                new JSR310Module(), new NotifyJacksonModule());

        // Master
        CouchSetup masterConfig = new CouchSetup(serversConfig.getMasterUrl())
                .setHttpClient(JerseyCouchHttpClient.class)
                .setJsonStrategy(jsonStrategy)
                .setUserName(serversConfig.getMasterUsername())
                .setPassword(serversConfig.getMasterPassword());
        masterServer = new CouchServer(masterConfig);
        masterDatabase = initMasterDatabase(serversConfig, masterServer);

        // Notification
        CouchSetup notificationConfig = new CouchSetup(serversConfig.getNotificationUrl())
                .setHttpClient(JerseyCouchHttpClient.class)
                .setJsonStrategy(jsonStrategy)
                .setUserName(serversConfig.getNotificationUsername())
                .setPassword(serversConfig.getNotificationPassword());
        notificationServer = new CouchServer(notificationConfig);

        // Request
        CouchSetup requestConfig = new CouchSetup(serversConfig.getRequestUrl())
                .setHttpClient(JerseyCouchHttpClient.class)
                .setJsonStrategy(jsonStrategy)
                .setUserName(serversConfig.getRequestUsername())
                .setPassword(serversConfig.getRequestPassword());
        requestServer = new CouchServer(requestConfig);
    }

    private CouchDatabase initMasterDatabase(CouchServersConfig serversConfig, CouchServer masterServer) {

        if (couchEnvironment.isTesting()) {
            CouchFeatureSet featureSet = CouchFeatureSet.builder().add(CouchFeature.ALLOW_DB_DELETE, true).build();
            CouchDatabase masterDatabaseForDelete = masterServer.database(serversConfig.getMasterDatabaseName(), featureSet);
            masterDatabaseForDelete.deleteDatabase();
        }

        // Hosted, only create if it does not exist.
        CouchDatabase localMasterDatabase = masterServer.database(serversConfig.getMasterDatabaseName());

        // Create database if it does not exist
        if (!localMasterDatabase.exists()) {
            WriteResponse createResponse = localMasterDatabase.createDatabase();
            if (createResponse.isError()) {
                String msg = String.format("Error creating master database (%s) - %s", localMasterDatabase.getDatabaseName(), createResponse.getErrorReason());
                throw ApiException.internalServerError(msg);
            }

            String[] designNames = new String[]{"DomainProfile", "Entity"};
            for (String designName : designNames) {
                String designPath = String.format("/couch/%s-design.json", designName);
                InputStream designStream = getClass().getResourceAsStream(designPath);
                if (designStream == null) {
                    String msg = String.format("Unable to find couch design file at: %s", designPath);
                    throw ApiException.internalServerError(msg);
                }

                try {
                    String designContent = IoUtils.toString(designStream);
                    WriteResponse response = localMasterDatabase.put().design(designName, designContent).execute();

                    if (response.isError()) {
                        String msg = String.format("Error creating views %s - %s", response.getHttpStatus(), response.getErrorReason());
                        throw ApiException.internalServerError(msg);
                    }
                } catch (IOException ex) {
                    String msg = "Error reading design file: " + designPath;
                    throw ApiException.internalServerError(ex, msg);
                }
            }
        }
        return localMasterDatabase;

    }

    public void deleteDomainDatabases(String domainName) {
        // if (couchEnvironment.isTesting() == false) {
        //   throw ApiException.badRequest("Databases can only be deleted in the test environment");
        // }

        CouchFeatureSet featureSet = CouchFeatureSet
                .builder()
                .add(CouchFeature.ALLOW_DB_DELETE, true)
                .build();

        // Notification DB.
        String notificationDbName = buildNotificationDbName(domainName);
        CouchDatabase notificationDatabase = notificationServer.database(notificationDbName, featureSet);
        if (notificationDatabase.exists()) {
            notificationDatabase.deleteDatabase();
        }

        // Request DB
        String requestDbName = buildRequestDbName(domainName);
        CouchDatabase requestDatabase = requestServer.database(requestDbName, featureSet);
        if (requestDatabase.exists()) {
            requestDatabase.deleteDatabase();
        }
    }

    public String buildRequestDbName(String domainName) {

        String prefix = this.requestDatabasePrefix;
        String suffix = this.requestDatabaseSuffix;

        return buildDbName(domainName, "notify-request-", prefix, suffix);
    }

    public String buildNotificationDbName(String domainName) {

        String prefix = this.notificationDatabasePrefix;
        String suffix = this.notificationDatabaseSuffix;

        return buildDbName(domainName, "notify-notification-", prefix, suffix);
    }

    protected String buildDbName(String domainName, String defaultPrefix, String prefix, String suffix) {
        if (StringUtil.isNotBlank(prefix) && StringUtil.isNotBlank(suffix)) {
            return prefix + domainName + suffix;
        } else if (StringUtil.isNotBlank(prefix)) {
            return prefix + domainName;
        } else if (StringUtil.isNotBlank(suffix)) {
            return domainName + suffix;
        } else {
            return defaultPrefix + domainName;
        }
    }

    public CouchServersConfig getServersConfig() {
        return serversConfig;
    }

    public CouchServer getMasterServer() {
        return masterServer;
    }

    public CouchDatabase getMasterDatabase() {
        return masterDatabase;
    }

    public CouchServer getNotificationServer() {
        return notificationServer;
    }

    public CouchServer getRequestServer() {
        return requestServer;
    }

    public String getNotificationDatabasePrefix() {
        return notificationDatabasePrefix;
    }

    public String getNotificationDatabaseSuffix() {
        return notificationDatabaseSuffix;
    }

    public String getRequestDatabasePrefix() {
        return requestDatabasePrefix;
    }

    public String getRequestDatabaseSuffix() {
        return requestDatabaseSuffix;
    }

    public CouchEnvironment getEnvironment() {
        return couchEnvironment;
    }
}
