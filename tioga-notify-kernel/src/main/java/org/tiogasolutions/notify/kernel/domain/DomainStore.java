package org.tiogasolutions.notify.kernel.domain;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.CouchServer;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.couchace.core.api.request.CouchFeatureSet;
import org.tiogasolutions.couchace.core.api.response.GetDocumentResponse;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.couchace.core.api.response.TextDocument;
import org.tiogasolutions.couchace.core.api.response.WriteResponse;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.common.AbstractStore;
import org.tiogasolutions.notify.kernel.config.CouchServers;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;
import org.tiogasolutions.notify.pub.common.TopicInfo;
import org.tiogasolutions.notify.pub.common.TraitInfo;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Component
public class DomainStore extends AbstractStore {

    private static final Logger log = LoggerFactory.getLogger(DomainStore.class);
    // HACK - using admin role here and should not.
    private static final String USER_JSON_TEMPLATE = "{\"_id\": \"%s\",\"name\": \"%s\",\"type\": \"user\",\"roles\": [],\"password\": \"%s\"}";
    private final CouchServers couchServers;
    private final CouchServer notificationCouchServer;
    private final CouchServer requestCouchServer;

    @Autowired
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

    /**
     * Find multiple domains with the same named. Used primarily to rectify problem that result in duplicate records.
     *
     * @param domainName the domain name to query for.
     * @return a query result containing the requested domains or an empty result if none are found.
     */
    public QueryResult<DomainProfileEntity> findByDomainNames(String domainName) {

        CouchViewQuery viewQuery = CouchViewQuery.builder("DomainProfile", "ByDomainName")
                .key(domainName)
                .build();

        GetEntityResponse<DomainProfileEntity> response = couchDatabase.get()
                .entity(DomainProfileEntity.class, viewQuery)
                .onError(r -> throwError(r, format("Failure retrieving active domain profile by name [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
                .execute();

        return ListQueryResult.newComplete(DomainProfileEntity.class, response.getEntityList());
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
            throw ApiNotFoundException.notFound("The specified domain does not exist.");
        } else if (response.getSize() > 1) {
            throw ApiNotFoundException.notFound("Multiple domain profiles found with name " + domainName);
        }

        return response.getFirstEntity();
    }

    public DomainSummary fetchSummary(String domainName) {

        // HACK - should this func. be here? Should it be done this way?
        DomainProfileEntity domainProfile = findByDomainName(domainName);
        CouchDatabase notificationDb = this.notificationDb(domainProfile);


        // Topic info
        CouchViewQuery viewQuery = CouchViewQuery.builder("Summary", "TopicInfo")
                .includeDocs(false)
                .group(true)
                .build();
        GetDocumentResponse response = notificationDb.get()
                .document(viewQuery)
                .onError(r -> throwError(r, format("Failure retrieving topic info for summary [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
                .execute();

        List<TopicInfo> topics = new ArrayList<>();
        List<TextDocument> documents = response.getDocumentList();
        for (TextDocument document : documents) {
            String key = document.getKey().getJsonValue().replaceAll("\"", "");
            TopicInfo topicInfo = new TopicInfo(key, document.getContentAsLong());
            topics.add(topicInfo);
        }

        // Topic info
        viewQuery = CouchViewQuery.builder("Summary", "TraitInfo")
                .includeDocs(false)
                .group(true)
                .build();
        response = notificationDb.get()
                .document(viewQuery)
                .onError(r -> throwError(r, format("Failure retrieving trait info for summary [%s] - %s", r.getHttpStatus(), r.getErrorReason())))
                .execute();
        if (response.isError()) {
            throw ApiException.fromCode(response.getHttpStatusCode(), "Error reading topic info" + response.getErrorContent().getError());
        }
        List<TraitInfo> traits = new ArrayList<>();
        documents = response.getDocumentList();
        for (TextDocument document : documents) {
            String key = document.getKey().getJsonValue().replaceAll("\"", "");
            TraitInfo traitInfo = new TraitInfo(key, document.getContentAsLong());
            traits.add(traitInfo);
        }

        return new DomainSummary(topics, traits);
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

        String notificationDbName = couchServers.buildNotificationDbName(domainName);

        String requestDbName = couchServers.buildRequestDbName(domainName);

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
     *
     * @param domainName  the name of the domain
     * @param apiKey      the api key for the domain
     * @param apiPassword the api password for the domain
     * @return the domain
     */
    public DomainProfile recreateDomain(String domainName, String apiKey, String apiPassword) {

        if (couchServers.getEnvironment().isTesting() == false) {
            throw ApiException.badRequest("Can only create domain in test environment");
        }

        // Delete mast entry, if it exists.
        if (hasDomain(domainName)) {
            DomainProfileEntity domainProfileEntity = findByDomainName(domainName);
            deleteDomain(domainProfileEntity);
        }

        // Create domain profile.
        DomainProfileEntity domainProfile = createDomain(domainName, apiKey, apiPassword);

        return domainProfile.toModel();
    }

    /**
     * Delete the domain and request and notification databases.
     *
     * @param domainProfile -
     */
    public void deleteDomain(DomainProfileEntity domainProfile) {

        // Delete mast entry, if it exists.
        couchDatabase.delete()
                .entity(domainProfile)
                .onError(r -> log.error(format("Failure deleting domain %s from master db [%s] - %s", domainProfile.getDomainName(), r.getHttpStatus(), r.getErrorReason())))
                .execute();

        couchServers.deleteDomainDatabases(domainProfile.getDomainName());
    }

    private void createNotifyDatabase(DomainProfileEntity domainProfile) {
        CouchDatabase couchDatabase = notificationDb(domainProfile);
        WriteResponse createResponse = couchDatabase.createDatabase();
        if (createResponse.isError()) {
            String msg = String.format("Error creating notify couch database [%s] - %s", domainProfile.getNotificationDbName(), createResponse.getErrorReason());
            throw ApiException.internalServerError(msg);
        }

        // Create the designs
        String[] designNames = new String[]{"Entity", "Notification", "Task", "Summary"};
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

    private void createRequestDatabase(DomainProfileEntity domainProfile) {
        CouchDatabase couchDatabase = requestDb(domainProfile);

        // Create the database
        WriteResponse createResponse = couchDatabase.createDatabase();
        if (createResponse.isError()) {
            String msg = String.format("Error creating request couch database [%s] - %s", domainProfile.getRequestDbName(), createResponse.getErrorReason());
            throw ApiException.internalServerError(msg);
        }

        // Create the designs
        String[] designNames = new String[]{"Entity", "NotificationRequest"};
        for (String designName : designNames) {
            String designPath = String.format("/couch/%s-design.json", designName);
            InputStream designStream = getClass().getResourceAsStream(designPath);
            if (designStream == null) {
                String msg = String.format("Unable to find design file at: %s", designPath);
                throw ApiException.internalServerError(msg);
            }
            try {
                String designContent = IoUtils.toString(designStream);
                WriteResponse response = couchDatabase.put().design(designName, designContent).execute();
                if (response.isError()) {
                    String msg = String.format("Error creating views %s - %s", response.getHttpStatus(), response.getErrorReason());
                    throw ApiException.internalServerError(msg);
                }
            } catch (IOException ex) {
                String msg = "Error reading design file: " + designPath;
                throw ApiException.internalServerError(msg);
            }
        }

        // Add the user.
        CouchServersConfig serversConfig = couchServers.getServersConfig();
        Configuration httpClientConfig = new ClientConfig();
        ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(httpClientConfig);
        Client client = clientBuilder.build();
        client.register(HttpAuthenticationFeature.basic(serversConfig.getRequestUsername(), serversConfig.getRequestPassword()));

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
     *
     * @param domainProfile -
     * @return -
     */
    public CouchDatabase requestDb(DomainProfile domainProfile) {
        return requestCouchServer.database(domainProfile.getRequestDbName());
    }

    /**
     * Used public
     *
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

}
