package org.tiogasolutions.notify.sender.couch;

import org.tiogasolutions.notify.notifier.send.SendNotificationRequest;
import org.tiogasolutions.notify.notifier.NotifierException;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequestJsonBuilder;
import org.tiogasolutions.notify.notifier.send.NotificationAttachment;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;
import org.tiogasolutions.notify.notifier.send.AbstractNotificationSender;
import org.tiogasolutions.notify.notifier.uuid.TimeUuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 1:10 AM
 */
public class CouchNotificationSender extends AbstractNotificationSender {

  private static final Logger log = LoggerFactory.getLogger(CouchNotificationSender.class);
  private final ExecutorService executorService;

  private final String couchUrl;
  private final String databaseName;
  private final String username;
  private final String password;

  public CouchNotificationSender(String couchUrl, String databaseName, String username, String password) {

    this.couchUrl = couchUrl;
    this.databaseName = databaseName;
    this.username = username;
    this.password = password;

    executorService = Executors.newSingleThreadExecutor();
    log.debug("Configured for couch database {} at {}", databaseName, couchUrl);
  }

  public CouchNotificationSender(CouchNotificationSenderSetup setup) {
    this(setup.getCouchUrl(),
      setup.getDatabaseName(),
      setup.getUsername(),
      setup.getPassword());
  }

  @Override
  public Future<SendNotificationResponse> send(SendNotificationRequest request) {
    return sendJaxRs(request);
  }

  private Future<SendNotificationResponse> sendJaxRs(SendNotificationRequest request) {

    Callable<SendNotificationResponse> callable = () -> {
      try {
        InputStream is;
        String requestId = TimeUuid.randomUUID().toString();
        Client client = ClientBuilder.newBuilder().build();
        SendNotificationRequest.Status status;

        if (request.getAttachments().isEmpty()) {
          // There are no attachments, we will assume the status to ready for processing.
          status = SendNotificationRequest.Status.READY;
          is = toInputStream(request, requestId, null, status, "}");
          putDocument(request, client, requestId, null, is, MediaType.APPLICATION_JSON);

        } else {
          // Just in case someone gave us a request that is in the wrong state, force it here.
          status = SendNotificationRequest.Status.SENDING;
          is = toInputStream(request, requestId, null, status, "}");
          String revision = putDocument(request, client, requestId, null, is, MediaType.APPLICATION_JSON);

          for (NotificationAttachment attachment : request.getAttachments()) {
            is = attachment.getInputStream();
            revision = putDocument(request, client, requestId, revision, is, attachment.getContentType(), attachment.getName());
          }

          // Now change status to READY.
          status = SendNotificationRequest.Status.READY;
          String suffix = generateAttachmentJson(request, client, requestId);
          is = toInputStream(request, requestId, revision, status, suffix);
          putDocument(request, client, requestId, revision, is, MediaType.APPLICATION_JSON);
        }

        return SendNotificationResponse.newSuccess(request);

      } catch (ProcessingException e) {
        callbacks.callFailure(e.getNotificationResponse());
        return e.getNotificationResponse();
      }
    };

    return executorService.submit(callable);
  }

  /***
   * Appears this methods generates the attachment meta portion of the couch document by re-fetching the request.
   *
   * @param request -
   * @param client -
   * @param requestId -
   * @return -
   * @throws ProcessingException -
   */
  protected String generateAttachmentJson(SendNotificationRequest request,
                                          Client client,
                                          String requestId) throws ProcessingException {

    UriBuilder uriBuilder = UriBuilder.fromUri(couchUrl).path(databaseName).path(requestId);

    Invocation.Builder builder = client.target(uriBuilder).request(MediaType.APPLICATION_JSON);
    builder.header("Authorization", getBasicAuthentication(username, password));
    builder.header("Content-Type", MediaType.APPLICATION_JSON);

    Response response = builder.get();

    if (response.getStatus() != 200) {
      String msg = String.format("%s: Unable to put request", response.getStatus());
      SendNotificationResponse notificationResponse = SendNotificationResponse.newFailure(request, new NotifierException(msg));
      throw new ProcessingException(notificationResponse);
    }

    String couchResponse = response.readEntity(String.class);
    Map<String,String> map = new JsonParser().parse(couchResponse);
    String attachments = map.get("_attachments");

    return ", \"_attachments\" : " + attachments + "}";
  }

  private String putDocument(SendNotificationRequest request,
                             Client client,
                             String requestId,
                             String revision,
                             InputStream content,
                             String contentType,
                             String...paths) throws ProcessingException {

    UriBuilder uriBuilder = UriBuilder.fromUri(couchUrl).path(databaseName).path(requestId);
    for (String path : paths) {
      uriBuilder.path(path);
    }
    if (revision != null) {
      uriBuilder.queryParam("rev", revision);
    }

    Invocation.Builder builder = client.target(uriBuilder).request(MediaType.APPLICATION_JSON);
    builder.header("Authorization", getBasicAuthentication(username, password));
    builder.header("Content-Type", contentType);

    Entity entity = Entity.entity(content, contentType);
    Response response = builder.put(entity);
    String couchResponse = response.readEntity(String.class);

    if (response.getStatus() != 201) {
      String msg = String.format("%s: Unable to put request%n%s", response.getStatus(), couchResponse);
      SendNotificationResponse notificationResponse = SendNotificationResponse.newFailure(request, new NotifierException(msg));
      throw new ProcessingException(notificationResponse);
    }

    return parseRevision(couchResponse);
  }

  private InputStream toInputStream(SendNotificationRequest request,
                                    String requestId,
                                    String revision,
                                    SendNotificationRequest.Status status,
                                    String attachmentJson) {

    String json = new SendNotificationRequestJsonBuilder().toJson(request, status);
    String prefix;

    if (revision == null) {
      prefix = "{\n" +
        "   \"_id\": \"" + requestId + "\",\n" +
        "   \"entityType\": \"NotificationRequest\",\n" +
        "   \"entity\": ";

    } else {
      prefix = "{\n" +
        "   \"_id\": \"" + requestId + "\",\n" +
        "   \"_rev\": \"" + revision + "\",\n" +
        "   \"entityType\": \"NotificationRequest\",\n" +
        "   \"entity\": ";
    }

    String content = prefix + json + attachmentJson;

    return new ByteArrayInputStream(content.getBytes());
  }

  private String parseRevision(String response) {
    String REV = "\"rev\"";

    int posA = response.indexOf(REV);
    if (posA < 0) throw new NotifierException("Cannot parse revision from couch response: " + response);

    posA = response.indexOf(":", posA+REV.length());
    if (posA < 0) throw new NotifierException("Cannot parse revision from couch response: " + response);

    posA = response.indexOf("\"", posA+1);
    if (posA < 0) throw new NotifierException("Cannot parse revision from couch response: " + response);

    int posB = response.indexOf("\"", posA+1);
    if (posA < 0) throw new NotifierException("Cannot parse revision from couch response: " + response);

    return response.substring(posA+1, posB);
  }

  private static String getBasicAuthentication(String username, String password) {
    try {
      String token = username + ":" + password;
      return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));

    } catch (UnsupportedEncodingException ex) {
      throw new IllegalStateException("Cannot encode with UTF-8", ex);
    }
  }

  public void dispose() {
    executorService.shutdown();
  }

  public static class ProcessingException extends Exception {
    private final SendNotificationResponse notificationResponse;
    public ProcessingException(SendNotificationResponse notificationResponse) { this.notificationResponse = notificationResponse; }
    public SendNotificationResponse getNotificationResponse() { return notificationResponse; }
  }

}
