package org.tiogasolutions.notify.kernel.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.CreateAttachment;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.pub.attachment.AttachmentHolder;
import org.tiogasolutions.notify.pub.attachment.AttachmentInfo;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;

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
            NotificationRequestStore requestStore = new NotificationRequestStore(requestDb);

            List<NotificationRequestEntity> readyRequests = requestStore.findByStatus(NotificationRequestStatus.READY);

            for (NotificationRequestEntity request : readyRequests) {
                NotificationRef notificationRef = null;
                try {
                    // Mark request as processing.
                    request.processing();
                    request = requestStore.saveAndReload(request);

                    // Create notification in the kernel.
                    CreateNotification createNotification = new CreateNotification(
                            request.isInternal(),
                            request.getTopic(),
                            request.getSummary(),
                            request.getTrackingId(),
                            request.getCreatedAt(),
                            request.getExceptionInfo(),
                            request.getLinks(),
                            request.getTraitMap());
                    notificationRef = notificationKernel.createNotification(createNotification);

                    // Create attachments.
                    for (AttachmentInfo attachmentInfo : request.listAttachmentInfo()) {
                        AttachmentHolder holder = requestStore.findAttachment(request.getRequestId(), attachmentInfo.getName());
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

    protected void handleFailure(NotificationRequestStore requestStore, NotificationRequestEntity request, NotificationRef notificationRef) {
        // TODO - use the notificationRef in dealing with the failure (this would mean the notification was created but attachments or something else FAILED).
        try {
            request.failed();
            requestStore.save(request);
        } catch (Throwable t) {
            log.error("Error in couch receiver handling failure.", t);
        }

    }
}
