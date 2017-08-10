package org.tiogasolutions.notify.engine.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.String.format;

public class PruneRequestsJob implements Runnable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private boolean running = false;
    private long requestsProcessed = 0;

    private final String domainName;
    private final NotificationRequestStore requestStore;

    public PruneRequestsJob(DomainKernel domainKernel, String domainName) {
        this.domainName = domainName;

        DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
        CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
        requestStore = new NotificationRequestStore(requestDb);
    }

    public void run() {
        running = true;

        try {
            deleteCompletedRequests(requestStore);
            deleteFailedRequests(requestStore);
            deleteProcessingRequests(requestStore);

        } catch (Exception e) {
            String msg = format("Exception deleting request for the domain %s.", domainName);
            log.error(msg, e);

        } finally {
            running = false;
            log.info("Finished pruning requests for the domain {}.", domainName);
        }
    }

    private void deleteCompletedRequests(NotificationRequestStore requestStore) {
        List<NotificationRequestEntity> requests = null;
        while (requests == null || requests.size() > 0) {
            requests = requestStore.findByStatus(NotificationRequestStatus.COMPLETED);
            log.info("Deleting {} \"completed\" requests for the domain {}.", requests.size(), domainName);

            for (NotificationRequestEntity request : requests) {
                requestsProcessed++;
                requestStore.deleteRequest(request.getRequestId());
            }
        }
    }

    private void deleteFailedRequests(NotificationRequestStore requestStore) {
        List<NotificationRequestEntity> requests = null;
        while (requests == null || requests.size() > 0) {
            requests = requestStore.findByStatus(NotificationRequestStatus.FAILED);
            log.info("Deleting {} \"failed\" requests for the domain {}.", requests.size(), domainName);

            for (NotificationRequestEntity request : requests) {
                requestsProcessed++;
                requestStore.deleteRequest(request.getRequestId());
            }
        }
    }

    private void deleteProcessingRequests(NotificationRequestStore requestStore) {
        List<NotificationRequestEntity> requests = null;
        while (requests == null || requests.size() > 0) {
            requests = requestStore.findByStatus(NotificationRequestStatus.PROCESSING, 100);
            log.info("Deleting {} 1-week-old \"processing\" requests for the domain {}.", requests.size(), domainName);

            for (NotificationRequestEntity request : requests) {
                if (request.getCreatedAt().isBefore(ZonedDateTime.now().minusDays(7))) {
                    requestsProcessed++;
                    requestStore.deleteRequest(request.getRequestId());
                }
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public Results getStartedResults() {
        return new Results(requestsProcessed, String.format("STARTED: Deleted %s requests from the domain %s.", requestsProcessed, domainName));
    }

    public Results getResults() {
        if (running) {
            return new Results(requestsProcessed, String.format("RUNNING: Deleted %s requests from the domain %s.", requestsProcessed, domainName));
        } else {
            return new Results(requestsProcessed, String.format("COMPLETED: Deleted %s requests from the domain %s.", requestsProcessed, domainName));
        }
    }

    public static class Results {

        private final String message;
        private final long requestsProcessed;

        public Results(long requestsProcessed, String message) {
            this.requestsProcessed = requestsProcessed;
            this.message = message;
        }

        public long getRequestsProcessed() {
            return requestsProcessed;
        }

        public String getMessage() {
            return message;
        }
    }
}
