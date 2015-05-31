package org.tiogasolutions.notify.kernel.config;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.couchace.core.api.CouchSetup;
import org.tiogasolutions.couchace.core.api.request.CouchFeature;
import org.tiogasolutions.couchace.core.api.request.CouchFeatureSet;
import org.tiogasolutions.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.couchace.core.internal.util.StringUtil;
import org.tiogasolutions.couchace.jackson.JacksonCouchJsonStrategy;
import org.tiogasolutions.couchace.jersey.JerseyCouchHttpClient;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.notify.kernel.jackson.NotifyKernelJacksonModule;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by harlan on 2/14/15.
 */
@Named
public class CouchServers {
  private final Environment environment;
  private final CouchServersConfig serversConfig;

  private final CouchServer masterServer;
  private final CouchDatabase masterDatabase;

  private final CouchServer notificationServer;
  private final String notificationDatabasePrefix;
  private final String notificationDatabaseSuffix;

  private final CouchServer requestServer;
  private final String requestDatabasePrefix;
  private final String requestDatabaseSuffix;

  @Inject
  public CouchServers(Environment environment, CouchServersConfig serversConfig) {
    this.environment = environment;
    this.serversConfig = serversConfig;

    this.notificationDatabasePrefix = serversConfig.getNotificationDatabasePrefix();
    this.notificationDatabaseSuffix = serversConfig.getNotificationDatabaseSuffix();

    this.requestDatabasePrefix = serversConfig.getRequestDatabasePrefix();
    this.requestDatabaseSuffix = serversConfig.getRequestDatabaseSuffix();

    // CouchJsonStrategy used by all.
    JacksonCouchJsonStrategy jsonStrategy = new JacksonCouchJsonStrategy(
        new JSR310Module(), new NotifyKernelJacksonModule());

    // Master
    CouchSetup masterConfig = new CouchSetup(serversConfig.getMasterUrl())
        .setHttpClient(JerseyCouchHttpClient.class)
        .setJsonStrategy(jsonStrategy)
        .setUserName(serversConfig.getMasterUserName())
        .setPassword(serversConfig.getMasterPassword());
    masterServer = new CouchServer(masterConfig);
    masterDatabase = initMasterDatabase(serversConfig, masterServer);

    // Notification
    CouchSetup notificationConfig = new CouchSetup(serversConfig.getNotificationUrl())
        .setHttpClient(JerseyCouchHttpClient.class)
        .setJsonStrategy(jsonStrategy)
        .setUserName(serversConfig.getNotificationUserName())
        .setPassword(serversConfig.getNotificationPassword());
    notificationServer = new CouchServer(notificationConfig);

    // Request
    CouchSetup requestConfig = new CouchSetup(serversConfig.getRequestUrl())
        .setHttpClient(JerseyCouchHttpClient.class)
        .setJsonStrategy(jsonStrategy)
        .setUserName(serversConfig.getRequestUserName())
        .setPassword(serversConfig.getRequestPassword());
    requestServer = new CouchServer(requestConfig);
  }

  private CouchDatabase initMasterDatabase(CouchServersConfig serversConfig, CouchServer masterServer) {

    if (isTestEnvironment()) {
      // Test, delete the database so we will recreate.
      CouchFeatureSet featureSet = CouchFeatureSet.builder().add(CouchFeature.ALLOW_DB_DELETE, true).build();
      CouchDatabase masterDatabaseForDelete = masterServer.database(serversConfig.getMasterDatabaseName(), featureSet);
      masterDatabaseForDelete.deleteDatabase();
    }

    // Hosted, only create if it does not exist.
    CouchDatabase localMasterDatabase = masterServer.database(serversConfig.getMasterDatabaseName());

    // Create database if it does not exist
    if (!localMasterDatabase.exists()) {
      localMasterDatabase.createDatabase();

      String[] designNames = new String[] {"DomainProfile", "Entity"};
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

          if(response.isError()) {
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
    if (!isTestEnvironment()) {
      throw ApiException.badRequest("Can only create databases in test environment");
    }

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

  public String buildDbName(String domainName, String defaultPrefix, String prefix, String suffix) {
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

  public String buildRequestDbName(String domainName) {

    String prefix = this.requestDatabasePrefix;
    String suffix = this.requestDatabaseSuffix;

    if (StringUtil.isNotBlank(prefix) && StringUtil.isNotBlank(suffix)) {
      return prefix + domainName + suffix;
    } else if (StringUtil.isNotBlank(prefix)) {
      return prefix + domainName;
    } else if (StringUtil.isNotBlank(suffix)) {
      return domainName + suffix;
    } else {
      return domainName + "-notify-request";
    }
  }

  public String buildNotificationDbName(String domainName) {

    String prefix = this.notificationDatabasePrefix;
    String suffix = this.notificationDatabaseSuffix;

    if (StringUtil.isNotBlank(prefix) && StringUtil.isNotBlank(suffix)) {
      return prefix + domainName + suffix;
    } else if (StringUtil.isNotBlank(prefix)) {
      return prefix + domainName;
    } else if (StringUtil.isNotBlank(suffix)) {
      return domainName + suffix;
    } else {
      return domainName + "-notify";
    }
  }

  public boolean isTestEnvironment() {
    return environment.acceptsProfiles("test") && !environment.acceptsProfiles("hosted");
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
}
