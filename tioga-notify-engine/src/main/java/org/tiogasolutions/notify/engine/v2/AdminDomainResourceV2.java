package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.lib.hal.HalLinks;
import org.tiogasolutions.lib.hal.HalLinksBuilder;
import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationDomain;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;
import org.tiogasolutions.notify.pub.task.TaskQuery;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

public class AdminDomainResourceV2 {

    private static final Logger log = LoggerFactory.getLogger(AdminDomainResourceV2.class);

    private final PubUtils pubUtils;
    private final ExecutionManager em;
    private final String domainName;

    public AdminDomainResourceV2(PubUtils pubUtils, ExecutionManager em, String domainName) {
        this.pubUtils = pubUtils;
        this.em = em;
        this.domainName = domainName;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainProfile() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);

        HalItem item = pubUtils.fromDomainProfile(HttpStatusCode.CREATED, domainProfile);
        return pubUtils.toResponse(item).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteDomain() {
        em.getDomainKernel().deleteDomain(domainName);

        HalLinks links = HalLinksBuilder.builder()
                .create("domains", pubUtils.uriAdminDomains())
                .build();

        HalItem item = new HalItem(HttpStatusCode.OK, links);
        return pubUtils.toResponse(item).build();
    }

    @GET
    @Path("/summary")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainSummary() {
        DomainSummary summary = em.getDomainKernel().fetchSummary(domainName);
        return Response.ok(summary).build();
    }

    @Path("/notifications")
    public NotificationsResourceV2 getNotificationsResourceV1(@Context Request request) {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new NotificationsResourceV2(request, em);
    }

    @Path("/route-catalog")
    public RouteCatalogResourceV2 getRouteCatalogResourceV1() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new RouteCatalogResourceV2(em);
    }

    @Path("/requests")
    public NotificationRequestResourceV2 getRequestResourceV1() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new NotificationRequestResourceV2(em);
    }

    @Path("/tasks")
    public TasksResourceV2 getTasksResourceV1() {
        DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
        em.newApiContext(domainProfile);
        return new TasksResourceV2(em);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prune/requests")
    public Response pruneRequests() {

        final DomainProfile domainProfile = em.getDomainKernel().findByDomainName(domainName);
        final CouchDatabase requestDb = em.getDomainKernel().requestDb(domainProfile);
        final NotificationRequestStore requestStore = new NotificationRequestStore(requestDb);

        new Thread( () -> deleteRequests(requestStore)).start();

        JobResults results = new JobResults(format("Deleting all requests from the domain %s.", domainName));
        return Response.accepted(results).build();
    }

    private static void deleteRequests(NotificationRequestStore requestStore) {
        try {
            deleteCompletedRequests(requestStore);
            deleteFailedRequests(requestStore);
            deleteProcessingRequests(requestStore);

        } catch (Exception e) {
            log.error("Exception deleting request.", e);
        }
        log.error("Finished pruning requests.");
    }

    private static void deleteCompletedRequests(NotificationRequestStore requestStore) {
        List<NotificationRequestEntity> requests = null;
        while (requests == null || requests.size() > 0) {
            requests = getRequests(requestStore, NotificationRequestStatus.COMPLETED);
            log.error("Deleting {} \"completed\" requests.", requests.size());

            for (NotificationRequestEntity request : requests) {
                requestStore.deleteRequest(request.getRequestId());
            }
        }
    }

    private static void deleteFailedRequests(NotificationRequestStore requestStore) {
        List<NotificationRequestEntity> requests = null;
        while (requests == null || requests.size() > 0) {
            requests = getRequests(requestStore, NotificationRequestStatus.FAILED);
            log.error("Deleting {} \"failed\" requests.", requests.size());

            for (NotificationRequestEntity request : requests) {
                requestStore.deleteRequest(request.getRequestId());
            }
        }
    }

    private static void deleteProcessingRequests(NotificationRequestStore requestStore) {
        List<NotificationRequestEntity> requests = null;
        while (requests == null || requests.size() > 0) {
            requests = getRequests(requestStore, NotificationRequestStatus.PROCESSING);
            log.error("Deleting {} 1-week-old \"processing\" requests.", requests.size());

            for (NotificationRequestEntity request : requests) {
                if (request.getCreatedAt().isBefore(ZonedDateTime.now().minusDays(7))) {
                    log.error("Deleting request {} created on {}.", request.getRequestId(), request.getCreatedAt());
                    requestStore.deleteRequest(request.getRequestId());
                }
            }
        }
    }

    private static List<NotificationRequestEntity> getRequests(NotificationRequestStore requestStore, NotificationRequestStatus status) {
        try {
            return requestStore.findByStatus(status, 100);

        } catch (ApiNotFoundException e) {
            return Collections.emptyList();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prune/notifications")
    public Response pruneNotifications() {

        new Thread( () -> deleteNotifications(em.getDomainKernel(), domainName)).start();

        JobResults results = new JobResults(format("Deleting all notifications and tasks from the domain %s.", domainName));
        return Response.accepted(results).build();
    }

    private static void deleteNotifications(DomainKernel domainKernel, String domainName) {
        try {
            NotificationDomain notificationDomain = domainKernel.notificationDomain(domainName);

            List<Notification> notifications = null;
            while (notifications == null || notifications.size() > 0) {
                notifications = getNotifications(notificationDomain);
                log.error("Deleting {} notifications.", notifications.size());

                next: for (Notification notification : notifications) {
                    List<TaskEntity> tasks = getTaskForNotification(notificationDomain, notification);
                    log.error("Deleting {} related tasks.", notifications.size());

                    // Test the tasks - if any are sending or pending skip everything.
                    for (TaskEntity task : tasks) {
                        if (task.getTaskStatus().isSending() || task.getTaskStatus().isPending()) {
                            continue next; // Skip it, it's still processing.
                        }
                    }

                    // OK, no issues, so delete all the tests.
                    for (TaskEntity task : tasks) {
                        notificationDomain.deleteTask(task.getTaskId());
                    }

                    // And lastly, delete the notification
                    notificationDomain.deleteNotification(notification.getNotificationId());
                }
            }

            // Now it is completely possible that there are tasks out there that are
            // orphaned - their notification doesn't exist. Let's take them out next...
            List<TaskEntity> tasks = notificationDomain.query(new TaskQuery().setLimit(100)).getResults();
            log.error("Deleting {} abandoned tasks.", notifications.size());

            for (TaskEntity task : tasks) {
                if (task.getTaskStatus().isCompleted() || task.getTaskStatus().isFailed()) {
                    notificationDomain.deleteTask(task.getTaskId());
                }
            }

        } catch (Exception e) {
            log.error("Exception deleting notifications.", e);
        }
        log.error("Finished pruning notifications.");
    }

    private static List<Notification> getNotifications(NotificationDomain notificationDomain) {
        try {
            NotificationQuery noteQuery = new NotificationQuery().setLimit(100);
            return notificationDomain.query(noteQuery).getResults();

        } catch (ApiNotFoundException e) {
            log.error("No tasks for the domain {}.", notificationDomain);
            return Collections.emptyList();
        }
    }

    private static List<TaskEntity> getTaskForNotification(NotificationDomain notificationDomain, Notification notification) {
        try {
            TaskQuery taskQuery = new TaskQuery().setNotificationId(notification.getNotificationId());
            return notificationDomain.query(taskQuery).getResults();

        } catch (ApiNotFoundException e) {
            return Collections.emptyList();
        }
    }

    public static class JobResults {

        private final String message;

        JobResults(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
