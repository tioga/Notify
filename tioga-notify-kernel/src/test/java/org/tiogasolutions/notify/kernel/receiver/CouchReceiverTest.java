package org.tiogasolutions.notify.kernel.receiver;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.TestFactory;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.request.LqRequestEntity;
import org.tiogasolutions.notify.notifier.request.LqRequest;
import org.tiogasolutions.notify.pub.*;
import org.tiogasolutions.notify.kernel.KernelAbstractTest;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus;
import org.tiogasolutions.notify.kernel.request.LqRequestStore;
import org.tiogasolutions.notify.notifier.request.LqResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.testng.Assert.*;

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

  @Inject
  private DomainKernel domainKernel;
  @Inject
  private ExecutionManager executionManager;
  @Inject
  private NotificationKernel notificationKernel;

  private LqRequestStore requestStore;
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
    requestStore = new LqRequestStore(requestDatabase);

  }

  private void assertNotification(LqResponse response) {
    LqRequest lqRequest = response.getRequest();

    // Request should be ready.
    LqRequestEntity requestEntity = requestStore.findByTrackingId(lqRequest.getTrackingId());
    assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertEquals(requestEntity.getRequestStatus(), LqRequestEntityStatus.READY);

    // Run the receiver
    receiver.receiveRequests(domainProfile);

    // Request should now be COMPLETED.
    requestEntity = requestStore.findByTrackingId(lqRequest.getTrackingId());
    assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertEquals(requestEntity.getRequestStatus(), LqRequestEntityStatus.COMPLETED);

    // From here on need an execution context for the test domain

    try {
      executionManager.newApiContext(domainProfile);

      // Now should have a notification
      NotificationQuery notificationQuery = new NotificationQuery()
          .setTrackingId(lqRequest.getTrackingId());
      QueryResult<Notification> result = notificationKernel.query(notificationQuery);
      assertEquals(result.getSize(), 1);
      Notification notification = result.getFirst();
      assertEquals(notification.getTopic(), lqRequest.getTopic());
      assertEquals(notification.getSummary(), lqRequest.getSummary());
      assertNotNull(notification.getExceptionInfo());
      assertEquals(notification.getExceptionInfo().getMessage(), lqRequest.getExceptionInfo().getMessage());

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

}
