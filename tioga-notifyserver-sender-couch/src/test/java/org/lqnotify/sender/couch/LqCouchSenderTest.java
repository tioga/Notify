package org.lqnotify.sender.couch;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.lqnotify.kernel.KernelAbstractTest;
import org.lqnotify.kernel.TestFactory;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.request.LqRequestEntity;
import org.lqnotify.kernel.request.LqRequestEntityStatus;
import org.lqnotify.kernel.request.LqRequestStore;
import org.lqnotify.notifier.LqNotifier;
import org.lqnotify.notifier.request.LqRequest;
import org.lqnotify.notifier.request.LqResponse;
import org.lqnotify.notifier.request.LqResponseType;
import org.lqnotify.pub.DomainProfile;
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
public class LqCouchSenderTest extends KernelAbstractTest {

  @Inject
  private DomainKernel domainKernel;

  private static int lastTrackingId = 4400;

  private static String nextTrackingId() {
    return String.valueOf(lastTrackingId++);
  }

  private LqNotifier notifier;
  private LqRequestStore requestStore;

  @BeforeClass
  public void setup() {
    DomainProfile domainProfile = domainKernel.findByApiKey(TestFactory.API_KEY);
    CouchDatabase requestDb = domainKernel.requestDb(domainProfile);
    requestStore = new LqRequestStore(requestDb);

    LqCouchSenderSetup couchSenderSetup = new LqCouchSenderSetup(
      requestDb.getHttpClient().getBaseUrl(),
      requestDb.getDatabaseName(),
      domainProfile.getApiKey(),
      domainProfile.getApiPassword()
    );

    LqCouchSender sender = new LqCouchSender(couchSenderSetup);
    sender.onFailure(f -> fail("Failure in sending request: " + f.getThrowable().getMessage()));
    sender.onFailure(f -> fail("Failure in sending attachment: " + f.getThrowable().getMessage()));

    notifier = new LqNotifier(sender);
    notifier.onBegin(b -> b.topic("test topic").trackingId(nextTrackingId()));
  }

  public void requestEntityLifeCycle() throws Exception {

    // Send a notification
    Future<LqResponse> responseFuture = notifier.begin()
        .summary("Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("attachOne", MediaType.TEXT_PLAIN, "this is attachment one")
        .attach("attachTwo", MediaType.TEXT_PLAIN, "this is attachment two")
        .send();

    LqResponse response = responseFuture.get();
    assertEquals(response.getResponseType(), LqResponseType.SUCCESS);
    assertNotificationCreated(response.getRequest());

  }

  private void assertNotificationCreated(LqRequest lqRequest) {

    // Retrieve the LqRequestEntity and verify.
    assertNotNull(lqRequest.getTrackingId());
    LqRequestEntity requestEntity = requestStore.findByTrackingId(lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertTrue(requestEntity.getCreatedAt().isEqual(lqRequest.getCreatedAt()));
    Assert.assertEquals(requestEntity.getSummary(), lqRequest.getSummary());
    Assert.assertEquals(requestEntity.getTrackingId(), lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getRequestStatus(), LqRequestEntityStatus.READY);
    Assert.assertEquals(requestEntity.listAttachmentInfo().size(), 2);
    assertTrue(requestEntity.listAttachmentInfo().stream().anyMatch(a -> a.getName().equals("attachOne")));
    assertTrue(requestEntity.listAttachmentInfo().stream().anyMatch(a -> a.getName().equals("attachTwo")));

    // Retrieve by requestId
    requestEntity = requestStore.findByRequestId(requestEntity.getRequestId());
    Assert.assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertTrue(requestEntity.getCreatedAt().isEqual(lqRequest.getCreatedAt()));
    Assert.assertEquals(requestEntity.getSummary(), lqRequest.getSummary());
    Assert.assertEquals(requestEntity.getTrackingId(), lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getRequestStatus(), LqRequestEntityStatus.READY);

    // Check attachments
    requestEntity = requestStore.findByRequestId(requestEntity.getRequestId());
    Assert.assertEquals(requestEntity.getTopic(), lqRequest.getTopic());
    assertTrue(requestEntity.getCreatedAt().isEqual(lqRequest.getCreatedAt()));
    Assert.assertEquals(requestEntity.getSummary(), lqRequest.getSummary());
    Assert.assertEquals(requestEntity.getTrackingId(), lqRequest.getTrackingId());
    Assert.assertEquals(requestEntity.getRequestStatus(), LqRequestEntityStatus.READY);

    // Mark processing.
    requestEntity.processing();
    requestEntity = requestStore.saveAndReload(requestEntity);
    Assert.assertEquals(requestEntity.getRequestStatus(), LqRequestEntityStatus.PROCESSING);

    // Mark Completed.
    requestEntity.completed();
    requestEntity = requestStore.saveAndReload(requestEntity);
    Assert.assertEquals(requestEntity.getRequestStatus(), LqRequestEntityStatus.COMPLETED);

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
    Future<LqResponse> responseFuture = notifier.begin()
        .summary("Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("attachOne", MediaType.TEXT_PLAIN, "this is attachment one")
        .attach("attachTwo", MediaType.TEXT_PLAIN, "this is attachment two")
        .send();
    LqResponse response = responseFuture.get();
    LqRequest request1 = response.getRequest();
    assertEquals(response.getResponseType(), LqResponseType.SUCCESS);
    responseFuture = notifier.begin()
        .summary("Another Test message")
        .trait("key1", "value1")
        .exception(new Throwable("Some kind of trouble"))
        .attach("attachOne", MediaType.TEXT_PLAIN, "this is another attachment one")
        .attach("attachTwo", MediaType.TEXT_PLAIN, "this is another attachment two")
        .send();
    response = responseFuture.get();
    assertEquals(response.getResponseType(), LqResponseType.SUCCESS);
    LqRequest request2 = response.getRequest();
    assertEquals(response.getResponseType(), LqResponseType.SUCCESS);

    // Query for ready, should only find two.
    List<LqRequestEntity> readyRequests = requestStore.findByStatus(LqRequestEntityStatus.READY);
    assertEquals(readyRequests.size(), 2);
    assertTrue(readyRequests.stream().anyMatch(r -> r.getTrackingId().equals(request1.getTrackingId())));
    assertTrue(readyRequests.stream().anyMatch(r -> r.getTrackingId().equals(request2.getTrackingId())));

    // Mark one as processing.
    LqRequestEntity entity = readyRequests.get(0);
    entity.processing();
    requestStore.save(entity);

    // Query for ready, should only find one.
    readyRequests = requestStore.findByStatus(LqRequestEntityStatus.READY);
    assertEquals(readyRequests.size(), 1);
    assertTrue(readyRequests.stream().anyMatch(r -> r.getTrackingId().equals(request2.getTrackingId())));
  }
}
