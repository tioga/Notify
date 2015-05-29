package org.tiogasolutions.notify.client;

import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.id.uuid.TimeUuid;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.sender.couch.CouchNotificationSender;
import org.tiogasolutions.notify.notifier.builder.NotificationBuilder;
import org.tiogasolutions.notify.notifier.request.NotificationResponse;
import org.tiogasolutions.notify.notifier.sender.LoggingNotificationSender;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.sender.couch.CouchNotificationSenderSetup;
import org.tiogasolutions.notify.sender.http.HttpNotificationSender;
import org.tiogasolutions.notify.sender.http.HttpNotificationSenderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

public class SwingAdminApp extends TestMainSupport {

  private static final Logger log = LoggerFactory.getLogger(SwingAdminApp.class);

  public static void main(String[] args) {
    try {
      new SwingAdminApp().run();
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public SwingAdminApp() {
    Configuration httpClientConfig = new ClientConfig();
    ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(httpClientConfig);
    client = clientBuilder.build();

    // Credentials for the Admin API
    client.register(HttpAuthenticationFeature.basic("admin", "North2South!"));
  }

  private void run() throws Exception {

    // The first thing we need to do is to establish which domain name we are working with.
    this.domainName = System.getProperties().getProperty("user.name");
    if ("jacob".equals(domainName)) domainName = "jacobp";

    domainName = JOptionPane.showInputDialog("New/Existing domain name:", domainName);
    if (domainName == null) return;

    initDomainName(domainName);

    DomainProfile domainProfile = getOrCreateDomainProfile(client, domainName);
    this.apiKey = domainProfile.getApiKey();
    this.apiPassword = domainProfile.getApiPassword();

    // The next thing to do is identify which operation to execute.
    Map<String,Operation> operationsMap = new LinkedHashMap<>();
    operationsMap.put("Status", this::checkStatus);
    operationsMap.put("Generate Requests (Couch Sender)", this::generateRequestsByCouch);
    operationsMap.put("Generate Requests (HTTP Sender)", this::generateRequestsByHttp);
    operationsMap.put("Update Catalog", this::updateCatalog);
    operationsMap.put("Kill 'em All", this::deleteAll);
    operationsMap.put("Delete Tasks", this::deleteTasks);
    operationsMap.put("Delete Requests", this::deleteRequests);
    operationsMap.put("Delete Notifications", this::deleteNotifications);

    String name = null;

    while(true) {
      name = (String)JOptionPane.showInputDialog(null,
        "Please select an admin operation",
        "Select Operation",
        JOptionPane.QUESTION_MESSAGE,
        null,
        ReflectUtils.toArray(String.class, operationsMap.keySet()),
        name);

      if (name == null) break;
      operationsMap.get(name).execute();

    };
  }

  public void deleteAll() throws Exception {
    deleteRequests();
    deleteNotifications();
    deleteTasks();
  }

  public void deleteTasks() throws Exception {

    int count = 0;

    try {
      QueryResult<Task> result;
      do {
        result = getTasks(null);
        count += result.getSize();
        result.getResults().forEach((task) -> deleteTask(domainName, task));
      } while (result.isNotEmpty());
    } catch (RuntimeJsonMappingException e) {
      // HACK / TODO - remove once QueryResult deserialization bug is fixed.
    }

    String msg = String.format("%s task(s) were deleted.", count);
    JOptionPane.showMessageDialog(null, msg, "Tasks Deleted", JOptionPane.INFORMATION_MESSAGE);
  }

  public void deleteNotifications() throws Exception {
    int count = 0;

    try {
      QueryResult<Notification> result;
      do {
        result = getNotifications(domainName);
        count += result.getSize();
        result.getResults().forEach((request) -> deleteNotification(domainName, request));
      } while (result.isNotEmpty());

    } catch (RuntimeJsonMappingException e) {
      // HACK / TODO - remove once QueryResult deserialization bug is fixed.
    }

    String msg = String.format("%s notification(s) were deleted.", count);
    JOptionPane.showMessageDialog(null, msg, "Notifications Deleted", JOptionPane.INFORMATION_MESSAGE);
  }

  public void deleteRequests() throws Exception {
    int count = 0;

    try {
      QueryResult<NotificationRequest> result;
      do {
        result = getRequests(domainName, null);
        count += result.getSize();
        result.getResults().forEach((request) -> deleteRequest(domainName, request));
      } while (result.isNotEmpty());

    } catch (RuntimeJsonMappingException e) {
      // HACK / TODO - remove once QueryResult deserialization bug is fixed.
    }

    String msg = String.format("%s request(s) were deleted.", count);
    JOptionPane.showMessageDialog(null, msg, "Requests Deleted", JOptionPane.INFORMATION_MESSAGE);
  }

  private void updateCatalog() throws Exception {

    File currentDir = new File("").getAbsoluteFile();
    File configDir = new File(currentDir, "/runtime/config/");
    String fileName = String.format("route-catalog-%s.json", domainName);
    File jsonFile = new File(configDir, fileName);
    String json = IoUtils.toString(jsonFile);

    Response response = client.target(apiPath + "/v1/admin/domains").path(domainName).path("route-catalog")
      .request(MediaType.APPLICATION_JSON_TYPE)
      .post(Entity.entity(json, MediaType.APPLICATION_JSON_TYPE));

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.OK) {
      throw ApiException.fromCode(statusCode, "Update of route catalog FAILED: " + response.getStatusInfo());
    }

    JOptionPane.showMessageDialog(null, "The catalog has been updated:\n" + jsonFile, "Catalog Updated", JOptionPane.INFORMATION_MESSAGE);
  }

  public void generateRequestsByHttp() throws Exception {

    HttpNotificationSenderConfig config = new HttpNotificationSenderConfig()
        .setUrl(apiPath + "/v1/client/requests")
        .setUserName(apiKey)
        .setPassword(apiPassword);

    LoggingNotificationSender simpleSender = new LoggingNotificationSender();

    HttpNotificationSender httpSender = new HttpNotificationSender(config);
    httpSender.onResponse(r -> simpleSender.send(r.getRequest()));

    generateRequests(new Notifier(httpSender));
    httpSender.dispose();
  }

  public void checkStatus() throws Exception {
    Response response = client.target(apiPath + "/v1/status")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .get();

    HttpStatusCode statusCode = HttpStatusCode.findByCode(response.getStatus());
    if (statusCode != HttpStatusCode.OK) {
      throw ApiException.fromCode(statusCode, "Get of tasks FAILED: " + response.getStatusInfo());
    }

    String json = response.readEntity(String.class);

    JOptionPane.showMessageDialog(null, json, "Status: " + statusCode, JOptionPane.INFORMATION_MESSAGE);
  }

  public void generateRequestsByCouch() throws Exception {

    CouchNotificationSender sender = new CouchNotificationSender(couchSenderSetup);

    // noinspection ThrowableResultOfMethodCallIgnored
    sender.onFailure(f -> throwError("Failure in SENDING request: " + f.getThrowable().getMessage()));

    // noinspection ThrowableResultOfMethodCallIgnored
    sender.onFailure(f -> throwError("Failure in SENDING attachment: " + f.getThrowable().getMessage()));

    generateRequests(new Notifier(sender));
    sender.dispose();
  }

  private void generateRequests(Notifier notifier) {
    String countString = JOptionPane.showInputDialog("How many task should we create?", "1");
    if (countString == null) return;
    int notificationsToSend = Integer.valueOf(countString.trim());

    notifier.onBegin(b -> b.topic("hn-test").trackingId(TimeUuid.randomUUID().toString()));

    List<Future<NotificationResponse>> futures = new ArrayList<>();

    // Send notifications
    for(int i=0; i<notificationsToSend; i++) {
      NotificationBuilder builder = notifier.begin()
        .topic("Swing Admin App")
        .summary("Here is a longer summary message. There really is no maximum length but it would be awkward for it to be too long: " + i)
        .trait("key1", "value1")
        .trait("index", String.valueOf(i))
        .trait("no_value_key", null)
//          .trait("ReallyLongKeyName", "AndAreallyLong value")
        .link("example", "http://example.com")
        .link("Tioga YouTrack", "http://tioga.myjetbrains.com/")
        .exception(new Throwable("This is notification exception"))
        .attach("attachOne", MediaType.TEXT_PLAIN, "Test main attachment one")
        .attach("attachTwo", MediaType.TEXT_PLAIN, "Test main attachment two");

      Future<NotificationResponse> future = builder.send();

      futures.add(future);
      log.debug("Notification " + i);
    }

    while (futures.isEmpty() == false) {
      for (Future future : futures.toArray(new Future[futures.size()])) {
        if (future.isDone()) {
          futures.remove(future);
        }
      }
    }

    startReceiver();
  }

  private static void throwError(String msg) {
    System.out.println(msg);
    System.out.flush();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static interface Operation {
    public void execute() throws Exception;
  }

  private void initDomainName(String domainName) throws IOException {

    if ("jacobp".equals(domainName)) {

      apiPath = "http://localhost:39011/notify-server/api";
      couchSenderSetup = new CouchNotificationSenderSetup(
        "http://localhost:5984",
        "tioga-notify-jacobp-request",
        "app-user",
        "app-user");

    } else if ("proto".equals(domainName)) {

      apiPath = "https://proto.stcg.net/notify-server/api";
      couchSenderSetup = new CouchNotificationSenderSetup(
        "http://proto.stcg.net:5984",
        "tioga-notify-proto-request",
        "app-user",
        "app-user");

    } else {

      DomainProfile domainProfile = getOrCreateDomainProfile(client, domainName);

      apiPath = "http://localhost:8080/notify-server/api";
      couchSenderSetup = new CouchNotificationSenderSetup(
        "http://localhost:5984",
        domainProfile.getRequestDbName(),
        domainProfile.getApiKey(),
        domainProfile.getApiPassword());
    }
  }
}
