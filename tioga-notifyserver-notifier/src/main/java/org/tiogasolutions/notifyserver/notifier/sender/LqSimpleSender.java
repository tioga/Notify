package org.tiogasolutions.notifyserver.notifier.sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.notifyserver.notifier.json.LqRequestJsonBuilder;
import org.tiogasolutions.notifyserver.notifier.request.LqRequest;
import org.tiogasolutions.notifyserver.notifier.request.LqRequestStatus;
import org.tiogasolutions.notifyserver.notifier.request.LqResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:52 AM
 */
public class LqSimpleSender extends LqAbstractSender {
  private static final Logger log = LoggerFactory.getLogger(LqSimpleSender.class);

  private LqRequest lastRequest;

  @Override
  public Future<LqResponse> send(LqRequest request) {

    this.lastRequest = request;
    if (log.isTraceEnabled()) {
      log.trace(new LqRequestJsonBuilder().toJson(request, LqRequestStatus.READY));
    } else {
      log.debug("Notification {}:{}", lastRequest.getTopic(), lastRequest.getSummary());
    }
    LqResponse response = LqResponse.newSuccess(request);
    callbacks.callSuccess(response);
    return CompletableFuture.completedFuture(response);
  }

  public LqRequest getLastRequest() {
    return lastRequest;
  }

  public void clearLast() {
    lastRequest = null;
  }
}
