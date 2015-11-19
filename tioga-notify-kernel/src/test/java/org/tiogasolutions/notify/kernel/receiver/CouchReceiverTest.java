package org.tiogasolutions.notify.kernel.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.notify.kernel.KernelAbstractTest;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.request.NotificationRequestStore;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

/**
 * User: Harlan
 * Date: 2/8/2015
 * Time: 1:44 AM
 */
@Test
public class CouchReceiverTest extends KernelAbstractTest {

  private static int lastTrackingId = 999900;

  private static String nextTrackingId() {
    return String.valueOf(lastTrackingId++);
  }

  @Autowired
  private DomainKernel domainKernel;
  @Autowired
  private ExecutionManager executionManager;
  @Autowired
  private NotificationKernel notificationKernel;

  private NotificationRequestStore requestStore;
  private CouchRequestReceiver receiver;
  private DomainProfile domainProfile;
  private CouchDatabase requestDatabase;

  @BeforeClass
  public void setup() {

    // Create receiver.
    receiver = new CouchRequestReceiver(domainKernel, notificationKernel, executionManager);

    // Domain profile we will use for testing.
    domainProfile = domainKernel.findByApiKey(TestFactory.API_KEY);

    // Create sender and sender store.
    requestDatabase = domainKernel.requestDb(domainProfile);
    requestStore = new NotificationRequestStore(requestDatabase);

  }

/* HACK - at some point this was no longer being used, should re-enable - HN
  private void assertNotification(NotificationResponse response) {
    NotificationRequest notificationRequest = response.getRequest();

    // Request should be ready.
    NotificationRequestEntity notificationRequestEntity = requestStore.findByTrackingId(notificationRequest.getTrackingId());
    assertEquals(notificationRequestEntity.getTopic(), notificationRequest.getTopic());
    assertEquals(notificationRequestEntity.getRequestStatus(), NotificationRequestStatus.READY);

    // Run the receiver
    receiver.receiveRequests(domainProfile);

    // Request should now be COMPLETED.
    notificationRequestEntity = requestStore.findByTrackingId(notificationRequest.getTrackingId());
    assertEquals(notificationRequestEntity.getTopic(), notificationRequest.getTopic());
    assertEquals(notificationRequestEntity.getRequestStatus(), NotificationRequestStatus.COMPLETED);

    // From here on need an execution context for the test domain

    try {
      executionManager.newApiContext(domainProfile);

      // Now should have a notification
      NotificationQuery notificationQuery = new NotificationQuery()
          .setTrackingId(notificationRequest.getTrackingId());
      QueryResult<Notification> result = notificationKernel.query(notificationQuery);
      assertEquals(result.getSize(), 1);
      Notification notification = result.getFirst();
      assertEquals(notification.getTopic(), notificationRequest.getTopic());
      assertEquals(notification.getSummary(), notificationRequest.getSummary());
      assertNotNull(notification.getExceptionInfo());
      assertEquals(notification.getExceptionInfo().getMessage(), notificationRequest.getExceptionInfo().getMessage());

      // Notification should also have two attachments.
      List<AttachmentInfo> attachmentInfoList = notification.getAttachmentInfoList();
      assertEquals(attachmentInfoList.size(), 2);
      AttachmentInfo attachmentInfo1 = attachmentInfoList.get(0);
      AttachmentInfo attachmentInfo2 = attachmentInfoList.get(1);
      assertEquals(attachmentInfo1.getName(), "attachOne");
      assertEquals(attachmentInfo2.getName(), "attachTwo");

      // Attachment one content
      AttachmentQuery attachQuery = new AttachmentQuery()
          .forNotification(notification)
          .setAttachmentName(attachmentInfo1.getName());
      AttachmentHolder attachmentHolder = notificationKernel.query(attachQuery);
      assertEquals(attachmentHolder.getContentType(), MediaType.TEXT_PLAIN);
      assertEquals(new String(attachmentHolder.getContent()), "this is attachment one");

      // Attachment one content
      attachQuery = new AttachmentQuery()
          .forNotification(notification)
          .setAttachmentName(attachmentInfo2.getName());
      attachmentHolder = notificationKernel.query(attachQuery);
      assertEquals(attachmentHolder.getContentType(), MediaType.TEXT_PLAIN);
      assertEquals(new String(attachmentHolder.getContent()), "this is attachment two");

    } finally {
      executionManager.clearContext();
    }
  }
*/

}
