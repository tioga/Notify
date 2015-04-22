package org.tiogasolutions.notify.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.notify.pub.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by jacobp on 3/6/2015.
 */
public class TestMainSupport {

  protected Client client;
  protected String domainName;
  protected String apiKey;
  protected String apiPassword;

  public DomainProfile getOrCreateDomainProfile(Client client, String domainName) throws IOException {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/domains").path(domainName)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .put(Entity.entity("", MediaType.WILDCARD_TYPE));

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.OK) {
      throw ApiException.fromCode(statusCode, "Put of domain FAILED: " + response.getStatusInfo());
    }

    String json = response.readEntity(String.class);
    TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(json, DomainProfile.class);
  }

  @SuppressWarnings("unchecked")
  public QueryResult<Task> getTasks(TaskStatus taskStatus) throws IOException {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/domains").path(domainName).path("tasks")
      .queryParam("taskStatus", taskStatus)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.OK) {
      throw ApiException.fromCode(statusCode, "Get of tasks FAILED: " + response.getStatusInfo());
    }

    String json = response.readEntity(String.class);
    TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(json, QueryResult.class);
  }

  @SuppressWarnings("unchecked")
  public QueryResult<Notification> getNotifications(String domainName) throws IOException {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/domains").path(domainName).path("notifications")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.OK) {
      throw ApiException.fromCode(statusCode, "Get of tasks FAILED: " + response.getStatusInfo());
    }

    String json = response.readEntity(String.class);
    TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(json, QueryResult.class);
  }

  public void deleteNotification(String domainName, Notification notification) {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/domains").path(domainName).path("notifications").path(notification.getNotificationId())
      .request(MediaType.APPLICATION_JSON_TYPE)
      .delete();

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.NO_CONTENT) {
      throw ApiException.fromCode(statusCode, "Delete of notification FAILED: " + response.getStatusInfo());
    }
  }

  @SuppressWarnings("unchecked")
  public QueryResult<Request> getRequests(String domainName, RequestStatus requestStatus) throws IOException {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/domains").path(domainName).path("requests")
      .queryParam("requestStatus", requestStatus)
      .request(MediaType.APPLICATION_JSON_TYPE)
      .get();

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.OK) {
      throw ApiException.fromCode(statusCode, "Get of tasks FAILED: " + response.getStatusInfo());
    }

    String json = response.readEntity(String.class);
    TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(json, QueryResult.class);
  }

  public void deleteTask(String domainName, Task task) {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/domains").path(domainName).path("tasks").path(task.getTaskId())
      .request(MediaType.APPLICATION_JSON_TYPE)
      .delete();

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.NO_CONTENT) {
      throw ApiException.fromCode(statusCode, "Delete of tasks FAILED: " + response.getStatusInfo());
    }
  }

  public void deleteRequest(String domainName, Request request) {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/domains").path(domainName).path("requests").path(request.getRequestId())
      .request(MediaType.APPLICATION_JSON_TYPE)
      .delete();

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.NO_CONTENT) {
      throw ApiException.fromCode(statusCode, "Delete of request FAILED: " + response.getStatusInfo());
    }
  }

  public void startReceiver() {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/system/request-receiver/actions/execute")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .post(Entity.entity("", MediaType.WILDCARD_TYPE));

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.NO_CONTENT) {
      throw ApiException.fromCode(statusCode, "Start of request receiver failed: " + response.getStatusInfo());
    }
  }

  public void startProcessor() {
    Response response = client.target("http://localhost:8080/lq-server/api/v1/admin/system/task-processor/actions/execute")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .post(Entity.entity("", MediaType.WILDCARD_TYPE));

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.NO_CONTENT) {
      throw ApiException.fromCode(statusCode, "Start of task processor failed: " + response.getStatusInfo());
    }
  }
}
