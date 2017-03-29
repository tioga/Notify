package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.attachment.AttachmentHolder;
import org.tiogasolutions.notify.pub.attachment.AttachmentQuery;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class NotificationsResourceV2 {

    private final ExecutionManager executionManager;

    public NotificationsResourceV2(Request request, ExecutionManager executionManager) {

        this.executionManager = executionManager;

        if ("OPTIONS".equals(request.getMethod()) == false && executionManager.hasContext() == false) {
            String msg = String.format("The execution context does not exist.");
            throw new UnsupportedOperationException(msg);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public QueryResult<Notification> getNotifications(@QueryParam("offset") int offset,
                                                      @QueryParam("limit") int limit,
                                                      @QueryParam("notificationId") String notificationId,
                                                      @QueryParam("topic") String topic,
                                                      @QueryParam("summary") String summary,
                                                      @QueryParam("traitKey") String traitKey,
                                                      @QueryParam("traitValue") String traitValue) {
        if (limit == 0) limit = 10;

        NotificationQuery query = new NotificationQuery()
                .setLimit(limit)
                .setOffset(offset)
                .setNotificationId(notificationId)
                .setTopic(topic)
                .setTraitKey(traitKey)
                .setSummary(summary)
                .setTraitValue(traitValue);

        return executionManager.getNotificationKernel().query(query);
    }

    @DELETE
    @Path("/{notificationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNotification(@PathParam("notificationId") String notificationId) {
        executionManager.getNotificationKernel().deleteNotification(notificationId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{notificationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotification(@PathParam("notificationId") String notificationId) {
        try {
            Notification notification = executionManager.getNotificationKernel().findNotificationById(notificationId);
            return Response.ok(notification).build();

        } catch (ApiNotFoundException e) {
            return Response.status(404).entity(e).build();
        }
    }

    @GET
    @Path("/{notificationId}/exception-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExceptionInfo(@PathParam("notificationId") String notificationId) {
        try {
            Notification notification = executionManager.getNotificationKernel().findNotificationById(notificationId);
            ExceptionInfo exception = notification.getExceptionInfo();
            if (exception == null) {
                String msg = String.format("Exception info not found for notification %s.", notificationId);
                ApiNotFoundException e = ApiException.notFound(msg);
                return Response.status(404).entity(e).build();

            } else {
                return Response.ok(exception).build();
            }
        } catch (ApiNotFoundException e) {
            return Response.status(404).entity(e).build();
        }
    }

    @GET
    @Path("/{notificationId}/attachments/{attachmentName}")
    public Response getAttachment(@PathParam("notificationId") String notificationId,
                                  @PathParam("attachmentName") String attachmentName) throws IOException {
        AttachmentQuery query = new AttachmentQuery()
                .setNotificationId(notificationId)
                .setAttachmentName(attachmentName);
        AttachmentHolder attachmentHolder = executionManager.getNotificationKernel().query(query);

//    Entity<byte[]> entity = Entity.entity(attachmentHolder.getContent(), attachmentHolder.getContentType());
        //return Response.ok().entity(entity).build();
        return Response.ok(attachmentHolder.getContent(), attachmentHolder.getContentType()).build();
    }

}
