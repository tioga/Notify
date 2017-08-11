package org.tiogasolutions.notify.notifier.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 1:52 AM
 */
public class LoggingNotificationSender extends AbstractNotificationSender {
    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationSender.class);

    private SendNotificationRequest lastRequest;

    @Override
    public Future<SendNotificationResponse> send(SendNotificationRequest request) {

        this.lastRequest = request;
        if (log.isTraceEnabled()) {
            log.trace(new SendNotificationRequestJsonBuilder().toJson(request, SendNotificationRequest.Status.READY));

        } else if (lastRequest.isInternal()) {
            log.debug("Internal Notification {}:{}", lastRequest.getTopic(), lastRequest.getSummary());

        } else {
            log.debug("Notification {}:{}", lastRequest.getTopic(), lastRequest.getSummary());
        }
        SendNotificationResponse response = SendNotificationResponse.newSuccess(request);
        callbacks.callSuccess(response);
        return CompletableFuture.completedFuture(response);
    }

    public SendNotificationRequest getLastRequest() {
        return lastRequest;
    }

    public void clearLast() {
        lastRequest = null;
    }
}
