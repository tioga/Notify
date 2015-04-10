package org.tiogasolutions.notify.sender.couch;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.testng.Assert.*;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 10:30 PM
 */
@Test
public class LqCouchSenderTest extends org.tiogasolutions.notify.kernel.KernelAbstractTest {

  @Inject
  private org.tiogasolutions.notify.kernel.domain.DomainKernel domainKernel;

  private static int lastTrackingId = 4400;

  private static String nextTrackingId() {
    return String.valueOf(lastTrackingId++);
  }

  private org.tiogasolutions.notify.notifier.LqNotifier notifier;
  private org.tiogasolutions.notify.kernel.request.LqRequestStore requestStore;

  @BeforeClass
  public void setup() {
    org.tiogasolutions.notify.pub.DomainProfile domainProfile = domainKernel.findByApiKey(org.tiogasolutions.notify.kernel.TestFactory.API_KEY);
    CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
    requestStore = new org.tiogasolutions.notify.kernel.request.LqRequestStore(requestDb);

    LqCouchSenderSetup couchSenderSetup = new LqCouchSenderSetup(
      requestDb.getHttpClient().getBaseUrl(),
      requestDb.getDatabaseName(),
      domainProfile.getApiKey(),
      domainProfile.getApiPassword()
    );

    LqCouchSender sender = new LqCouchSender(couchSenderSetup);
    sender.onFailure(f -> fail("Failure in sending request: " + f.getThrowable().getMessage()));
    sender.onFailure(f -> fail("Failure in sending attachment: " + f.getThrowable().getMessage()));

    notifier = new org.tiogasolutions.notify.notifier.LqNotifier(sender);
    notifier.onBegin(b -> b.topic("test topic").trackingId(nextTrackingId()));
  }

  public void requestEntityLifeCycle() throws Exception {

    // Send a notification
    Future<org.tiogasolutions.notify.notifier.request.LqResponse> responseFuture = notifier.begin()
        .summary("Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("attachOne", MediaType.TEXT_PLAIN, "this is attachment one")
        .attach("attachTwo", MediaType.TEXT_PLAIN, "this is attachment two")
        .send();

    org.tiogasolutions.notify.notifier.request.LqResponse response = responseFuture.get();
    assertEquals(response.getResponseType(), org.tiogasolutions.notify.notifier.request.LqResponseType.SUCCESS);
    assertNotificationCreated(response.getRequest());

  }

  private void assertNotificationCreated(org.tiogasolutions.notify.notifier.request.LqRequest lqRequest) {

    // Retrieve the LqRequestEntity and verify.
    assertNotNull(lqRequest.getTrackingId());
    org.tiogasolutions.notify.kernel.request.LqRequestEntity requestEntity = requestStore.findByTrackingId(lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertTrue(requestEntity.getCreatedAt().isEqual(lqRequest.getCreatedAt()));
    Assert.assertEquals(requestEntity.getSummary(), lqRequest.getSummary());
    Assert.assertEquals(requestEntity.getTrackingId(), lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getRequestStatus(), org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus.READY);
    Assert.assertEquals(requestEntity.listAttachmentInfo().size(), 2);
    assertTrue(requestEntity.listAttachmentInfo().stream().anyMatch(a -> a.getName().equals("attachOne")));
    assertTrue(requestEntity.listAttachmentInfo().stream().anyMatch(a -> a.getName().equals("attachTwo")));

    // Retrieve by requestId
    requestEntity = requestStore.findByRequestId(requestEntity.getRequestId());
    Assert.assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertTrue(requestEntity.getCreatedAt().isEqual(lqRequest.getCreatedAt()));
    Assert.assertEquals(requestEntity.getSummary(), lqRequest.getSummary());
    Assert.assertEquals(requestEntity.getTrackingId(), lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getRequestStatus(), org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus.READY);

    // Check attachments
    requestEntity = requestStore.findByRequestId(requestEntity.getRequestId());
    Assert.assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertTrue(requestEntity.getCreatedAt().isEqual(lqRequest.getCreatedAt()));
    Assert.assertEquals(requestEntity.getSummary(), lqRequest.getSummary());
    Assert.assertEquals(requestEntity.getTrackingId(), lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getRequestStatus(), org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus.READY);

    // Mark processing.
    requestEntity.processing();
    requestEntity = requestStore.saveAndReload(requestEntity);
    Assert.assertEquals(requestEntity.getRequestStatus(), org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus.PROCESSING);

    // Mark Completed.
    requestEntity.completed();
    requestEntity = requestStore.saveAndReload(requestEntity);
    Assert.assertEquals(requestEntity.getRequestStatus(), org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus.COMPLETED);

    // Check attachments
    requestEntity = requestStore.findByRequestId(requestEntity.getRequestId());
    Assert.assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertTrue(requestEntity.getCreatedAt().isEqual(lqRequest.getCreatedAt()));
    Assert.assertEquals(requestEntity.getSummary(), lqRequest.getSummary());
    Assert.assertEquals(requestEntity.getTrackingId(), lqRequest.getTrackingId());

  }


  @Test(dependsOnMethods = "requestEntityLifeCycle")
  public void processingQueries() throws ExecutionException, InterruptedException {
    // Send two notifications
    Future<org.tiogasolutions.notify.notifier.request.LqResponse> responseFuture = notifier.begin()
        .summary("Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("attachOne", MediaType.TEXT_PLAIN, "this is attachment one")
        .attach("attachTwo", MediaType.TEXT_PLAIN, "this is attachment two")
        .send();
    org.tiogasolutions.notify.notifier.request.LqResponse response = responseFuture.get();
    org.tiogasolutions.notify.notifier.request.LqRequest request1 = response.getRequest();
    assertEquals(response.getResponseType(), org.tiogasolutions.notify.notifier.request.LqResponseType.SUCCESS);
    responseFuture = notifier.begin()
        .summary("Another Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("attachOne", MediaType.TEXT_PLAIN, "this is another attachment one")
        .attach("attachTwo", MediaType.TEXT_PLAIN, "this is another attachment two")
        .send();
    response = responseFuture.get();
    assertEquals(response.getResponseType(), org.tiogasolutions.notify.notifier.request.LqResponseType.SUCCESS);
    org.tiogasolutions.notify.notifier.request.LqRequest request2 = response.getRequest();
    assertEquals(response.getResponseType(), org.tiogasolutions.notify.notifier.request.LqResponseType.SUCCESS);

    // Query for ready, should only find two.
    List<org.tiogasolutions.notify.kernel.request.LqRequestEntity> readyRequests = requestStore.findByStatus(org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus.READY);
    assertEquals(readyRequests.size(), 2);
    assertTrue(readyRequests.stream().anyMatch(r -> r.getTrackingId().equals(request1.getTrackingId())));
    assertTrue(readyRequests.stream().anyMatch(r -> r.getTrackingId().equals(request2.getTrackingId())));

    // Mark one as processing.
    org.tiogasolutions.notify.kernel.request.LqRequestEntity entity = readyRequests.get(0);
    entity.processing();
    requestStore.save(entity);

    // Query for ready, should only find one.
    readyRequests = requestStore.findByStatus(org.tiogasolutions.notify.kernel.request.LqRequestEntityStatus.READY);
    assertEquals(readyRequests.size(), 1);
    assertTrue(readyRequests.stream().anyMatch(r -> r.getTrackingId().equals(request2.getTrackingId())));
  }
}
