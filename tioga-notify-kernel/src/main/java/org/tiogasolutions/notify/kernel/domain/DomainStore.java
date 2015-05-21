package org.tiogasolutions.notify.kernel.domain;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.couchace.core.api.request.CouchFeature;
import org.tiogasolutions.couchace.core.api.request.CouchFeatureSet;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.tiogasolutions.notify.kernel.common.AbstractStore;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.kernel.config.CouchServers;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;
import org.tiogasolutions.notify.notifier.NotifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * User: Harlan
 * Date: 2/12/2015
 * Time: 9:46 PM
 */
@Named
public class DomainStore extends AbstractStore {

  private static final Logger log = LoggerFactory.getLogger(DomainStore.class);
  private final CouchServers couchServers;
  private final CouchServer notificationCouchServer;
  private final CouchServer requestCouchServer;

  @Inject
  public DomainStore(CouchServers couchServers) {
    super(couchServers.getMasterDatabase());

    this.couchServers = couchServers;
    this.notificationCouchServer = couchServers.getNotificationServer();
    this.requestCouchServer = couchServers.getRequestServer();
  }

  public boolean hasDomain(String domainName) {

    CouchViewQuery viewQuery = CouchViewQuery.builder("DomainProfile", "ByDomainName")
        .key(domainName)
        .build();

    GetEntityResponse<DomainProfileEntity> response = couchDatabase.get()
        .entity(DomainProfileEntity.class, viewQuery)
        .onError(r -> throwError(r, format("Failure retrieving active domain profile by name [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
        .execute();

    return response.isNotEmpty();
  }

  public DomainProfileEntity findByProfileId(String profileId) {

    GetEntityResponse<DomainProfileEntity> response = couchDatabase.get()
        .entity(DomainProfileEntity.class, profileId)
        .onError(r -> throwError(r, format("Failure retrieving domain profile in couch [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
        .onResponse(r -> throwIfNotFound(r, "Domain profile not found by profile id " + profileId))
        .execute();

    return response.getFirstEntity();
  }

  public DomainProfileEntity findByDomainName(String domainName) {

    CouchViewQuery viewQuery = CouchViewQuery.builder("DomainProfile", "ByDomainName")
        .key(domainName)
        .build();

    GetEntityResponse<DomainProfileEntity> response = couchDatabase.get()
        .entity(DomainProfileEntity.class, viewQuery)
        .onError(r -> throwError(r, format("Failure retrieving active domain profile by name [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
        .execute();

    if (response.isEmpty()) {
      throw ApiNotFoundException.notFound("Domain profile not found with name " + domainName);
    } else if (response.getSize() > 1) {
      throw ApiNotFoundException.notFound("Multiple domain profiles found with name " + domainName);
    }

    return response.getFirstEntity();
  }

  // TODO - need a DomainProfileQuery.
  public QueryResult<DomainProfileEntity> queryActive() {

    CouchViewQuery viewQuery = CouchViewQuery.builder("DomainProfile", "ByDomainStatus")
        .key("ACTIVE")
        .build();

    GetEntityResponse<DomainProfileEntity> response = couchDatabase.get()
        .entity(DomainProfileEntity.class, viewQuery)
        .onError(r -> throwError(r, format("Failure retrieving active domain profiles [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
        .execute();

    return ListQueryResult.newComplete(DomainProfileEntity.class, response.getEntityList());
  }

  public QueryResult<DomainProfileEntity> queryAll() {

    CouchViewQuery viewQuery = CouchViewQuery.builder("DomainProfile", "ByDomainStatus")
        .build();

    GetEntityResponse<DomainProfileEntity> response = couchDatabase.get()
        .entity(DomainProfileEntity.class, viewQuery)
        .onError(r -> throwError(r, format("Failure retrieving active domain profiles [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
        .execute();

    return ListQueryResult.newComplete(DomainProfileEntity.class, response.getEntityList());
  }

  public DomainProfileEntity createDomain(String domainName, String apiKey, String apiPassword) {

    String notificationDbName = buildDbName(
      domainName, "notifier-notification-",
      couchServers.getNotificationDatabasePrefix(),
      couchServers.getNotificationDatabaseSuffix());

    String requestDbName = buildDbName(
      domainName, "notifier-request-",
      couchServers.getRequestDatabasePrefix(),
      couchServers.getRequestDatabaseSuffix());

    // Create the profile entity
    DomainProfileEntity profileEntity = DomainProfileEntity.newEntity(
      domainName,
      apiKey,
      apiPassword,
      notificationDbName,
      requestDbName,
      RouteCatalog.newEmptyCatalog());

    profileEntity = save(profileEntity);

    // Create the notify and request databases
    createNotifyDatabase(profileEntity);
    createRequestDatabase(profileEntity);

    // Create the profile and store in couch and in the map.
    return profileEntity;
  }

  private String buildDbName(String domainName, String defaultPrefix, String prefix, String suffix) {
    if (prefix == null) prefix = defaultPrefix;
    if (suffix == null) suffix = "";
    return prefix+domainName+suffix;
  }

  public DomainProfileEntity save(DomainProfileEntity profileEntity) {
    // Store in couch.
    couchDatabase.put()
        .entity(profileEntity)
        .onError(r -> throwError(r, format("Failure storing domain profile in couch [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
        .execute();

    // TODO if we had a DomainProfileRef could return that here instead.
    return findByProfileId(profileEntity.getProfileId());
  }

  /**
   * Only used for testing, should use call createDomain(domainName)
   * TODO - assert we are in development or it's the system test domain
   * @param domainName the name of the domain
   * @param apiKey the api key for the domain
   * @param apiPassword the api password for the domain
   * @return the domain
   */
  public DomainProfile recreateDomain(String domainName, String apiKey, String apiPassword) {

    if (!couchServers.isTestEnvironment()) {
      throw ApiException.badRequest("Can only create domain in test environment");
    }

    deleteDomain(domainName);

    DomainProfileEntity domainProfile = createDomain(domainName, apiKey, apiPassword);

    return domainProfile.toModel();
  }

  private void deleteDomain(String domainName) {

    // Delete mast entry, if it exists.
    CouchViewQuery viewQuery = CouchViewQuery.builder("DomainProfile", "ByDomainName")
        .key(domainName)
        .build();
    GetEntityResponse<DomainProfileEntity> response = couchDatabase.get()
        .entity(DomainProfileEntity.class, viewQuery)
        .onError(r -> throwError(r, format("Failure retrieving active domain profile by name [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
        .execute();
    if (response.isNotEmpty()) {
      DomainProfileEntity domainProfile = response.getFirstEntity();
      couchDatabase.delete()
          .entity(domainProfile)
          .onError(r -> log.error(format("Failure deleting domain %s from master db [%s] - %s", domainProfile.getDomainName(), r.getHttpStatus(), r.getErrorReason())))
          .execute();
    }

    // HACK - hard coded for test pattern.
    // Delete any databases that exist - may exist even if master does not.
    CouchFeatureSet featureSet = CouchFeatureSet
        .builder()
        .add(CouchFeature.ALLOW_DB_DELETE, true)
        .build();
    CouchDatabase notificationDatabase = requestCouchServer.database("notify-request-" + domainName, featureSet);
    if (notificationDatabase.exists()) {
      notificationDatabase.deleteDatabase();
    }
    CouchDatabase requestDatabase = requestCouchServer.database("notify-notification-" + domainName, featureSet);
    if (requestDatabase.exists()) {
      requestDatabase.deleteDatabase();
    }
  }

  private void createNotifyDatabase(DomainProfileEntity domainProfile) {
    CouchDatabase couchDatabase = notificationDb(domainProfile);
    WriteResponse createResponse = couchDatabase.createDatabase();
    if (createResponse.isError()) {
      String msg = format("Exception creating notification database %s for domain %s: %s", couchDatabase.getDatabaseName(), domainProfile.getDomainName(), createResponse.getHttpStatus());
      throw new NotifierException(msg);
    }

//    try {
      // Need to create the file system to load from the jar (ZipFileSystemProvider does not do this on it's own).
      Map<String, String> env = new HashMap<>();
      env.put("create", "true");
//      URL designUrl = getClass().getClassLoader().getResource("couch");
//      if (designUrl == null) {
//        throw ApiException.internalServerError("Unable to find base couch url.");
//      }
//      if (designUrl.getProtocol().equalsIgnoreCase("jar")) {
//        try {
//          TODO - can this be improved?
//          Throws exception if not found
//          FileSystems.getFileSystem(designUrl.toURI());
//
//        } catch (FileSystemNotFoundException ex) {
//          FileSystems.newFileSystem(designUrl.toURI(), env);
//        }
//      }
//    } catch (IOException e) {
//      throw ApiException.internalServerError("Error accessing base design url.");
//    } catch (URISyntaxException e) {
//      throw ApiException.internalServerError(e, "Error accessing base design url.");
//    }

    // Create the designs
    String[] designNames = new String[] {"Entity", "Notification", "Task"};
    for (String designName : designNames) {
      String designPath = String.format("/couch/%s-design.json", designName);
      InputStream designStream = getClass().getResourceAsStream(designPath);
      if (designStream == null) {
        String msg = String.format("Unable to find couch design file at: %s", designPath);
        throw ApiException.internalServerError(msg);
      }
      try {
        String designContent = IoUtils.toString(designStream);
        WriteResponse response = couchDatabase.put().design(designName, designContent).execute();
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

  private void createRequestDatabase(DomainProfileEntity domainProfile) {
    CouchDatabase couchDatabase = requestDb(domainProfile);

    // Create the database
    WriteResponse createResponse = couchDatabase.createDatabase();
    if (createResponse.isError()) {
      throw new NotifierException("Exception creating notify request database: " + createResponse.getErrorReason());
    }

    // Create the designs
    String designPath = "/couch/NotificationRequest-design.json";
    // URL designUrl = getClass().getClassLoader().getResource(designPath);
    InputStream designStream = getClass().getResourceAsStream(designPath);
    if (designStream == null) {
      String msg = String.format("Unable to find design file at: %s", designPath);
      throw new NotifierException(msg);
    }
    try {
      String designContent = IoUtils.toString(designStream);
      WriteResponse response = couchDatabase.put().design("NotificationRequest", designContent).execute();
      if (response.isError()) {
        String msg = String.format("Error creating views %s - %s", response.getHttpStatus(), response.getErrorReason());
        throw new NotifierException(msg);
      }
    } catch (IOException ex) {
      String msg = "Error reading design file: " + designPath;
      throw new NotifierException(msg, ex);
    }

    // Add the user.
    CouchServersConfig serversConfig = couchServers.getServersConfig();
    Configuration httpClientConfig = new ClientConfig();
    ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(httpClientConfig);
    Client client = clientBuilder.build();
    client.register(HttpAuthenticationFeature.basic(serversConfig.getRequestUserName(), serversConfig.getRequestPassword()));

    // TODO - this functionality should be moved into CouchAce
    // Put the domain, which will perform a get or create
    String documentId = "org.couchdb.user:" + domainProfile.getApiKey();
    String couchBaseUrl = serversConfig.getRequestUrl();
    Response response = client.target(couchBaseUrl)
        .path("_users/" + documentId)
        .request()
        .get();
    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode.isNotFound()) {
      // User does not exist, add them.
      String userJson = format(USER_JSON_TEMPLATE, documentId, domainProfile.getApiKey(), domainProfile.getApiPassword());
      response = client.target(couchBaseUrl)
          .path("_users/" + documentId)
          .request()
          .put(Entity.entity(userJson, MediaType.WILDCARD_TYPE));
      statusCode = HttpStatusCode.findByCode(response.getStatus());

      if (statusCode.isError()) {
        throw ApiException.fromCode(statusCode, "Error adding user: " + response.readEntity(String.class));
      }
    }
  }

  /**
   * Used public
   * @param domainProfile -
   * @return -
   */
  public CouchDatabase requestDb(DomainProfile domainProfile) {
    return requestCouchServer.database(domainProfile.getRequestDbName());
  }

  /**
   * Used public
   * @param domainProfile -
   * @return -
   */
  public CouchDatabase notificationDb(DomainProfile domainProfile) {
    return notificationCouchServer.database(domainProfile.getNotificationDbName());
  }

  private CouchDatabase requestDb(DomainProfileEntity domainProfile) {
    return requestCouchServer.database(domainProfile.getRequestDbName());
  }

  private CouchDatabase notificationDb(DomainProfileEntity domainProfile) {
    return notificationCouchServer.database(domainProfile.getNotificationDbName());
  }

  private CouchDatabase requestDb(DomainProfileEntity domainProfile, CouchFeatureSet featureSet) {
    return requestCouchServer.database(domainProfile.getRequestDbName(), featureSet);
  }

  private CouchDatabase notificationDb(DomainProfileEntity domainProfile, CouchFeatureSet featureSet) {
    return notificationCouchServer.database(domainProfile.getNotificationDbName(), featureSet);
  }

  // HACK - using admin role here and should not.
  private static final String USER_JSON_TEMPLATE = "{\"_id\": \"%s\",\"name\": \"%s\",\"type\": \"user\",\"roles\": [],\"password\": \"%s\"}";
}
