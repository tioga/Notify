package org.lqnotify.engine.core.v1;

import org.crazyyak.dev.common.exceptions.ApiNotFoundException;
import org.lqnotify.kernel.execution.ExecutionManager;
import org.lqnotify.kernel.notification.NotificationKernel;
import org.lqnotify.pub.*;
import org.tiogasolutions.dev.domain.query.QueryResult;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class NotificationsResourceV1 {

  private final NotificationKernel notificationKernel;

  public NotificationsResourceV1(ExecutionManager executionManager, NotificationKernel notificationKernel) {
    this.notificationKernel = notificationKernel;

    if (executionManager.hasContext() == false) {
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

    return notificationKernel.query(query);
  }

  @DELETE
  @Path("/{notificationId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteNotification(@PathParam("notificationId") String notificationId) {
    notificationKernel.deleteNotification(notificationId);
    return Response.noContent().build();
  }

  @GET
  @Path("/{notificationId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getNotification(@PathParam("notificationId") String notificationId) {
    try {
      Notification notification = notificationKernel.findNotificationById(notificationId);
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
      Notification notification = notificationKernel.findNotificationById(notificationId);
      ExceptionInfo exception = notification.getExceptionInfo();
      if (exception == null) {
        String msg = String.format("Exception info not found for notification %s.", notificationId);
        ApiNotFoundException e = ApiNotFoundException.notFound(msg);
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
    AttachmentHolder attachmentHolder = notificationKernel.query(query);

//    Entity<byte[]> entity = Entity.entity(attachmentHolder.getContent(), attachmentHolder.getContentType());
    //return Response.ok().entity(entity).build();
    return Response.ok(attachmentHolder.getContent(), attachmentHolder.getContentType()).build();
  }

}
