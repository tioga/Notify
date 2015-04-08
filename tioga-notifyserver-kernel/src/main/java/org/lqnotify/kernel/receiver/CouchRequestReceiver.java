package org.lqnotify.kernel.receiver;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.lqnotify.kernel.LqPubUtils;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.execution.ExecutionManager;
import org.lqnotify.kernel.notification.CreateAttachment;
import org.lqnotify.kernel.notification.CreateNotification;
import org.lqnotify.kernel.notification.NotificationKernel;
import org.lqnotify.kernel.request.*;
import org.lqnotify.pub.DomainProfile;
import org.lqnotify.pub.ExceptionInfo;
import org.lqnotify.pub.NotificationRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 8:06 PM
 */
public class CouchRequestReceiver implements RequestReceiver {

  private static final Logger log = LoggerFactory.getLogger(CouchRequestReceiver.class);
  private final DomainKernel domainKernel;
  private final NotificationKernel notificationKernel;
  private final ExecutionManager executionManager;

  public CouchRequestReceiver(DomainKernel domainKernel,
                              NotificationKernel notificationKernel,
                              ExecutionManager executionManager) {
    this.domainKernel = domainKernel;
    this.notificationKernel = notificationKernel;
    this.executionManager = executionManager;
  }

  @Override
  public void receiveRequests(DomainProfile domainProfile) {

    try {
      executionManager.newApiContext(domainProfile);

      CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
      LqRequestStore requestStore = new LqRequestStore(requestDb);

      List<LqRequestEntity> readyRequests = requestStore.findByStatus(LqRequestEntityStatus.READY);

      for (LqRequestEntity request : readyRequests) {
        NotificationRef notificationRef = null;
        try {
          // Mark request as processing.
          request.processing();
          request = requestStore.saveAndReload(request);

          // Create notification in the kernel.
          ExceptionInfo exceptionInfo = LqPubUtils.toExceptionInfo(request.getExceptionInfo());

          CreateNotification createNotification = new CreateNotification(
              request.getTopic(),
              request.getSummary(),
              request.getTrackingId(),
              request.getCreatedAt(),
              exceptionInfo,
              request.getTraitMap());
          notificationRef = notificationKernel.createNotification(createNotification);

          // Create attachments.
          for (LqAttachmentInfo attachmentInfo : request.listAttachmentInfo()) {
            LqAttachmentHolder holder = requestStore.findAttachment(request.getRequestId(), attachmentInfo.getName());
            CreateAttachment createAttachment = new CreateAttachment(notificationRef, holder.getName(), holder.getContentType(), holder.getContent());
            notificationRef = notificationKernel.createAttachment(createAttachment);
          }

          // Mark request as COMPLETED.
          request.completed();
          requestStore.save(request);

        } catch (Throwable t) {
          log.error("Exception generating notification for request.", t);
          handleFailure(requestStore, request, notificationRef);
        }
      }

      if (readyRequests.isEmpty() == false) {
        log.info("Imported {} notifications for the domain {}.", readyRequests.size(), domainProfile.getDomainName());
      }

    } finally {
      executionManager.clearContext();
    }

  }

  protected void handleFailure(LqRequestStore requestStore, LqRequestEntity request, NotificationRef notificationRef) {
    // TODO - use the notificationRef in dealing with the failure (this would mean the notification was created but attachments or something else FAILED).
    try {
      request.failed();
      requestStore.save(request);
    } catch (Throwable t) {
      log.error("Error in couch receiver handling failure.", t);
    }

  }
}
