package org.lqnotify.sender.couch;

import org.lqnotify.notifier.LqException;
import org.lqnotify.notifier.json.LqRequestJsonBuilder;
import org.lqnotify.notifier.request.LqRequest;
import org.lqnotify.notifier.request.LqRequestStatus;
import org.lqnotify.notifier.request.LqResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 1:10 AM
 */
public class LqSimpleHttpSender extends LqHttpSender {
  private static final Logger log = LoggerFactory.getLogger(LqSimpleHttpSender.class);

  public LqSimpleHttpSender(LqHttpSenderConfig config) {
    super(config);
    log.info("Configured SimpleLqHttpSender for " + getBaseUrl());
  }

  @Override
  public Future<LqResponse> send(LqRequest request) {
    // Never throw an exception from here.

    Callable<LqResponse> callable = () -> {
      Response sendResponse;
      try {
        // Send the request
        sendResponse = sendRequest(request, LqRequestStatus.READY);

      } catch (Exception t) {
        LqResponse lqResponse = LqResponse.newFailure(request, t);
        callbacks.callFailure(lqResponse);
        log.error("Failure sending Lq notification request: ", t);
        return lqResponse;
      }

      int status = sendResponse.getStatus();
      if (status == 200 || status == 201) {

        // Request success
        LqResponse lqResponse = LqResponse.newSuccess(request);
        callbacks.callSuccess(lqResponse);
        return lqResponse;

      } else {
        // Request failure
        LqException ex = new LqException("Non successful response from send: " + sendResponse.getStatus());
        LqResponse lqResponse = LqResponse.newFailure(request, ex);
        this.callbacks.callFailure(lqResponse);
        return lqResponse;
      }
    };

    return getExecutorService().submit(callable);

  }

  @Override
  protected Response sendRequest(LqRequest request, LqRequestStatus status) {

    String json = new LqRequestJsonBuilder().toJson(request, status);

    // Jersey does not allow entity value to be null.
    Entity entity = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);
    WebTarget webTarget = getClient().target(getBaseUrl());

    return webTarget.request(MediaType.APPLICATION_JSON_TYPE)
        .header("Content-Type", MediaType.APPLICATION_JSON_TYPE)
        .post(entity);
  }




}
