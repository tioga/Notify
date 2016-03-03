package org.tiogasolutions.notify.extras.monitor;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.tiogasolutions.apis.cloudfoundry.CfClient;
import org.tiogasolutions.apis.cloudfoundry.pub.Event;
import org.tiogasolutions.apis.cloudfoundry.pub.EventResource;
import org.tiogasolutions.apis.cloudfoundry.pub.GetEventsResponse;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ApiUnauthorizedException;
import org.tiogasolutions.notify.notifier.send.SendNotificationRequest;
import org.tiogasolutions.notify.sender.couch.CouchNotificationSender;

import java.time.ZonedDateTime;
import java.util.*;

import static java.time.ZonedDateTime.*;
import static org.slf4j.LoggerFactory.getLogger;

public class TiogaMonitor implements Runnable {

  private static final Logger log = getLogger(TiogaMonitor.class);

  public static void main(String...args) {
    // Priority #1, configure default logging levels. This will be
    // overridden later when/if the logback.xml is found and loaded.
    AppUtils.initLogback(Level.WARN);

    // Assume we want by default INFO on when & how the grizzly server
    // is started. Possibly overwritten by logback.xml if used.
    AppUtils.setLogLevel(Level.INFO, TiogaMonitor.class);

    TiogaMonitor app = new TiogaMonitor();
    new Thread(app).start();
  }

  private final long retention;
  private final CfClient client;
  private final CouchNotificationSender sender;

  private final Map<String,ZonedDateTime> processed = new HashMap<>();

  public TiogaMonitor() {
    String value = EnvUtils.findProperty("tioga.monitor.retention", String.valueOf(10));
    retention = Long.valueOf(value);

    client = new CfClient();
    client.login(EnvUtils.requireProperty("tioga.cloud.foundry.username"),
                 EnvUtils.requireProperty("tioga.cloud.foundry.password"));

    String couchUrl =     EnvUtils.requireProperty("tioga.monitor.couch.url");;
    String couchDb =     EnvUtils.requireProperty("tioga.monitor.couch.db");;
    String username =     EnvUtils.requireProperty("tioga.monitor.couch.username");;
    String password =     EnvUtils.requireProperty("tioga.monitor.couch.password");;
    sender = new CouchNotificationSender(couchUrl, couchDb, username, password);
  }

  @Override
  public void run() {
    try {
      processEvents();

    } catch (Throwable e) {
      e.printStackTrace();
      System.err.flush();
    }

    System.err.flush();
    System.out.flush();
    System.exit(0);
  }

  private void processEvents() throws Exception {

    seedProcessedEvents();

    for (;;) {
      GetEventsResponse response = fetchEvents();
      response.getEventResources().forEach(this::processEvent);

      cleanCache();

      Runtime runtime = Runtime.getRuntime();
      String msg = String.format("Cache size: %,d, max memory: %,dMB, total: %,dMB, free: %,dMB",
          processed.size(),
          runtime.maxMemory()/1024/1024,
          runtime.totalMemory()/1024/1024,
          runtime.freeMemory()/1024/1024);
      log.info(msg);

      Thread.sleep(1000);
    }
  }

  private void processEvent(EventResource resource) {
    String id = resource.getMetadata().getGuid();
    ZonedDateTime createdAt = resource.getMetadata().getCreatedAt();
    Event event = resource.getEvent();

    if (processed.containsKey(id)) {
      log.debug("Skipping processed event:\n    Type: {} {}\n    Name: {}\n    Created: {}\n    ID: {}\n",
          event.getType(), event.getAction(), resource.getEvent().getActeeName(), createdAt, id);
      return; // Do not reprocess any old events
    }

    if (createdAt.isBefore(now().minusMinutes(retention))) {
      log.debug("Skipping old event:\n    Type: {} {}\n    Name: {}\n    Created: {}\n    ID: {}\n",
          event.getType(), event.getAction(), resource.getEvent().getActeeName(), createdAt, id);
      return; // Skips anything over X Minutes old
    }

    sendNotification(resource);
    processed.put(id, createdAt);
  }

  private void sendNotification(EventResource resource) {
    Map<String, String> traits = new HashMap<>();
    String summary = buildSummary(resource, traits);
    log.info("Processing event: " + summary);

    SendNotificationRequest request = new SendNotificationRequest(
        "cloud-foundry-events",
        summary,
        resource.getMetadata().getGuid(),
        resource.getEvent().getTimestamp(),
        traits,
        Collections.emptyList(),
        null,
        Collections.emptyList()
    );

    sender.onFailure(response -> {
      String msg = "Failed to send notification for " + resource.getEvent().getActeeName();
      log.error(msg, response.getThrowable());
    });

    sender.send(request);
  }

  private String buildSummary(EventResource resource, Map<String,String> traits) {
    Event event = resource.getEvent();

    String summary = String.format("%s %s", resource.getEvent().getActeeName(), event.getType());

    if (event.getAction() != null) {
      summary += " ";
      summary += event.getAction();
      traits.put("action", event.getAction());
    }

    if (resource.getMetadata().getExitDescription() != null) {
      summary += ": ";
      summary += resource.getMetadata().getExitDescription();
      traits.put("exit-description", resource.getMetadata().getExitDescription());
    }

    return summary;
  }

  private GetEventsResponse fetchEvents() throws Exception {
    try {
      log.debug("Fetching events");

      long start = System.currentTimeMillis();
      GetEventsResponse response = client.getApplicationEvents(0);
      long duration = System.currentTimeMillis() - start;

      log.debug("Fetched {} events in {} seconds", response.getEventResources().size(),  duration/1000);

      return response;

    } catch (ApiUnauthorizedException e) {
      log.info("Refreshing access token");
      client.refresh();     // refresh our access token
      return fetchEvents(); // try again

    } catch (ApiException ex) {
      Thread.sleep(1000);
      log.error("Failed to retrieve events", ex);
      return fetchEvents(); // try again
    }
  }

  private void cleanCache() {

    List<String> ids = new ArrayList<>(processed.keySet());
    for (String id : ids) {

      ZonedDateTime createdAt = processed.get(id);
      if (createdAt == null) continue;

      if (createdAt.isBefore(now().minusMinutes(retention))) {
        log.debug("Removing old event {} {}", id, createdAt);
        processed.remove(id);
      }
    }
  }

  private void seedProcessedEvents() throws Exception {
    log.info("Seeding initial list of processed events");
    GetEventsResponse response = fetchEvents();

    for (EventResource resource : response.getEventResources()) {
      String id = resource.getMetadata().getGuid();
      ZonedDateTime createdAt = resource.getEvent().getTimestamp();

      processed.put(id, createdAt);
    }

    cleanCache();
    log.info("Seeding completed, caching {} events", processed.size());
  }
}
