package org.tiogasolutions.notify.kernel.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.tiogasolutions.notify.test.AbstractSpringTest;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@Test
public class NotificationStoreTest extends AbstractSpringTest {

    @Autowired
    private ExecutionManager executionManager;

    @Autowired
    private NotificationKernel notificationKernel;

    private NotificationRef tenYearsAgoRef;

    @BeforeClass
    public void beforeClass() {
        executionManager.newApiContext(TestFactory.API_KEY);

        try {
            ZonedDateTime tenYearsAgo = ZonedDateTime.now().minusYears(10);
            // Create a few test notifications.
            CreateNotification create = new CreateNotification(
                    false,
                    "TEST_TOPIC_22",
                    "some message",
                    "store-test-9022",
                    tenYearsAgo,
                    null,
                    Collections.singletonList(new Link("example", "http://example.com")),
                    BeanUtils.toMap("fav_color:aqua", "xyz_test_key"));
            tenYearsAgoRef = notificationKernel.createNotification(create);
            create = new CreateNotification(
                    false,
                    "TEST_TOPIC_22",
                    "some message",
                    "store-test-9001",
                    ZonedDateTime.now(),
                    null,
                    Collections.singletonList(new Link("example", "http://example.com")),
                    BeanUtils.toMap("fav_color:purple", "XyZ_TeSt_KeY"));
            notificationKernel.createNotification(create);
            create = new CreateNotification(
                    false,
                    "TEST_TOPIC_23",
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
        NotificationQuery query = new NotificationQuery().setTopic("TEST_TOPIC_22");
        QueryResult<Notification> result = notificationKernel.query(query);
        assertEquals(result.getSize(), 2);

        // Ten years ago should be the second one
        assertEquals(result.getAt(1).getNotificationId(), tenYearsAgoRef.getNotificationId());

        query = new NotificationQuery().setTopic("TEST_TOPIC_23");
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

        query = new NotificationQuery().setTraitKey("fav_color");
        result = notificationKernel.query(query);
        assertEquals(result.getSize(), 2);
    }

    public void findByTraitKeyAndValue() {
        NotificationQuery query = new NotificationQuery().setTraitKey("fav_color").setTraitValue("purple");
        QueryResult<Notification> result = notificationKernel.query(query);
        assertEquals(result.getSize(), 1);

        query = new NotificationQuery().setTraitKey("fav_color").setTraitValue("aqua");
        result = notificationKernel.query(query);
        assertEquals(result.getSize(), 1);

        query = new NotificationQuery().setTraitKey("fav_color").setTraitValue("clear");
        result = notificationKernel.query(query);
        assertEquals(result.getSize(), 0);

    }


}