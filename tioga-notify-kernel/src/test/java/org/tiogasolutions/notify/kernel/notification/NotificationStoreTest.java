package org.tiogasolutions.notify.kernel.notification;

import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.KernelAbstractTest;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.testng.annotations.*;

import javax.inject.Inject;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class NotificationStoreTest extends KernelAbstractTest {
  @Inject
  private ExecutionManager executionManager;
  @Inject
  private NotificationKernel notificationKernel;

  private NotificationRef tenYearsAgoRef;

  @BeforeClass
  public void beforeClass() {
    executionManager.newApiContext(TestFactory.API_KEY);

    try {
      ZonedDateTime tenYearsAgo = ZonedDateTime.now().minusYears(10);
      // Create a few test notifications.
      CreateNotification create = new CreateNotification(
          "TEST_TOPIC_RED",
          "some message",
          "store-test-9022",
          tenYearsAgo,
          null,
          Collections.singletonList(new Link("example", "http://example.com")),
          BeanUtils.toMap("color:red", "xyz_test_key"));
      tenYearsAgoRef = notificationKernel.createNotification(create);
      create = new CreateNotification(
          "TEST_TOPIC_RED",
          "some message",
          "store-test-9001",
          ZonedDateTime.now(),
          null,
          Collections.singletonList(new Link("example", "http://example.com")),
          BeanUtils.toMap("color:green", "XyZ_TeSt_KeY"));
      notificationKernel.createNotification(create);
      create = new CreateNotification(
          "TEST_TOPIC_BLUE",
          "some message",
          "store-test-9002",
          ZonedDateTime.now(),
          null,
          Collections.singletonList(new Link("example", "http://example.com")),
          BeanUtils.toMap("xyz_test_key"));
      notificationKernel.createNotification(create);

    } finally {
      executionManager.clearContext();
    }

  }

  @BeforeMethod
  public void beforeMethod() {
    executionManager.newApiContext(TestFactory.API_KEY);
  }

  @AfterMethod
  public void afterMethod() {
    executionManager.clearContext();
  }

  public void findById() {
    Notification notification = notificationKernel.findNotificationById(tenYearsAgoRef.getNotificationId());
    assertNotNull(notification);
    assertEquals(notification.getRevision(), tenYearsAgoRef.getRevision());
  }

  public void findByTopic() {
    NotificationQuery query = new NotificationQuery().setTopic("TEST_TOPIC_RED");
    QueryResult<Notification> result = notificationKernel.query(query);
    assertEquals(result.getSize(), 2);

    // Ten years ago should be the second one
    assertEquals(result.getAt(1).getNotificationId(), tenYearsAgoRef.getNotificationId());

    query = new NotificationQuery().setTopic("TEST_TOPIC_BLUE");
    result = notificationKernel.query(query);
    assertEquals(result.getSize(), 1);

    query = new NotificationQuery().setTopic("JUNK");
    result = notificationKernel.query(query);
    assertEquals(result.getSize(), 0);

  }

  public void findByTrackingId() {
    NotificationQuery query = new NotificationQuery().setTrackingId("store-test-9022");
    QueryResult<Notification> result = notificationKernel.query(query);
    assertEquals(result.getSize(), 1);
    Notification notification = result.getAt(0);
    assertEquals(notification.getNotificationId(), tenYearsAgoRef.getNotificationId());

    query = new NotificationQuery().setTrackingId("JUNK");
    result = notificationKernel.query(query);
    assertEquals(result.getSize(), 0);

  }

  public void findByTraitKeyOnly() {
    NotificationQuery query = new NotificationQuery().setTraitKey("xyz_test_key");
    QueryResult<Notification> result = notificationKernel.query(query);
    assertEquals(result.getSize(), 3);

    query = new NotificationQuery().setTraitKey("color");
    result = notificationKernel.query(query);
    assertEquals(result.getSize(), 2);
  }

  public void findByTraitKeyAndValue() {
    NotificationQuery query = new NotificationQuery().setTraitKey("color").setTraitValue("red");
    QueryResult<Notification> result = notificationKernel.query(query);
    assertEquals(result.getSize(), 1);

    query = new NotificationQuery().setTraitKey("color").setTraitValue("green");
    result = notificationKernel.query(query);
    assertEquals(result.getSize(), 1);

    query = new NotificationQuery().setTraitKey("color").setTraitValue("black");
    result = notificationKernel.query(query);
    assertEquals(result.getSize(), 0);

  }


}