package org.tiogasolutions.notify.engine.v1.admin;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.notify.engine.AbstractEngineJaxRsTest;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.task.TaskQuery;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationQuery;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.*;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

@SuppressWarnings("unchecked")
@Test
public class AdminResourceV1Test extends AbstractEngineJaxRsTest {

  private final TiogaJacksonObjectMapper objectMapper;

  public AdminResourceV1Test() {
    objectMapper = new TiogaJacksonObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @BeforeMethod
  public void beforeClass() throws Exception {
    getExecutionManager().newApiContext(TestFactory.API_KEY);

    // I need to delete all the notifications and tasks

    for (Notification notification : getNotificationKernel().query(new NotificationQuery())) {
      getNotificationKernel().deleteNotification(notification.getNotificationId());
    }

    for (TaskEntity task : getNotificationKernel().query(new TaskQuery())) {
      getNotificationKernel().deleteTask(task.getTaskId());
    }
  }

  @AfterMethod
  public void afterClass() throws Exception {
    getExecutionManager().clearContext();
  }

  private Invocation.Builder request(WebTarget webTarget) {
    return webTarget.request()
        .header("Authorization", toHttpAuth("jacobp", "Testing123"));
  }
  
  public void test_api_v1_admin_$NoAuthorization() {
    String path = "/api/v1/admin";
    Response response = target(path).request().get();
    assertEquals(response.getStatus(), 401);
  }

  public void test_api_v1_admin_$BadAuthorization() {
    String path = "/api/v1/admin";
    Response response = request(target(path)).header("Authorization", toHttpAuth("bad","guy")).get();

    assertEquals(response.getStatus(), 401);
  }

  public void test_api_v1_admin() {
    String path = "/api/v1/admin";
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v1_admin_domains() {
    String path = "/api/v1/admin/domains";
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v1_admin_domains_KernelTest() {
    String path = String.format("/api/v1/admin/domains/%s", TestFactory.DOMAIN_NAME);
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v1_admin_domains_nobody() {
    String path = String.format("/api/v1/admin/domains/%s", "bogus");
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 404);
  }

  public void test_api_v1_admin_domains_KernelTest_notifications() throws Exception {

    for (int i = 0; i < 8; i++) {
      Map<String,String> traits = new HashMap<>();
      traits.put("index", String.valueOf(i));
      if (i < 6) traits.put("even", i%2 == 0 ? "true" : "false");

      CreateNotification create = new CreateNotification(
          "test-"+i, "This is task #"+i, "tracking #"+i,
          ZonedDateTime.now(), null, Collections.emptyList(), traits);
      getNotificationKernel().createNotification(create);
    }


    String path = String.format("/api/v1/admin/domains/%s/notifications", TestFactory.DOMAIN_NAME);
    Response response = request(target(path)).get();
    assertEquals(response.getStatus(), 200);
    String json = response.readEntity(String.class);
    QueryResult<Notification> result = objectMapper.readValue(json, QueryResult.class);
    assertEquals(result.getSize(), 8);
    assertEquals(result.getLimit(), 10);
    assertEquals(result.getOffset(), 0);
    // TODO - I believe the answer to this next one should be true.
    assertEquals(result.isTotalExact(), false);
    // Now make sure the five we found were the ones we created.
    assertEquals(result.getAt(0).getTopic(), "test-7");
    assertEquals(result.getAt(1).getTopic(), "test-6");
    assertEquals(result.getAt(2).getTopic(), "test-5");
    assertEquals(result.getAt(3).getTopic(), "test-4");
    assertEquals(result.getAt(4).getTopic(), "test-3");
    assertEquals(result.getAt(5).getTopic(), "test-2");
    assertEquals(result.getAt(6).getTopic(), "test-1");
    assertEquals(result.getAt(7).getTopic(), "test-0");


    // Test an offset and limit query
    path = String.format("/api/v1/admin/domains/%s/notifications", TestFactory.DOMAIN_NAME);
    response = request(target(path).queryParam("offset", 3).queryParam("limit", 5)).get();
    assertEquals(response.getStatus(), 200);
    json = response.readEntity(String.class);
    result = objectMapper.readValue(json, QueryResult.class);
    assertEquals(result.getSize(), 5);
    assertEquals(result.getLimit(), 5);
    assertEquals(result.getOffset(), 3);
    assertEquals(result.isTotalExact(), false);
    // Now make sure the five we found were the ones we created.
    assertEquals(result.getAt(0).getTopic(), "test-4");
    assertEquals(result.getAt(1).getTopic(), "test-3");
    assertEquals(result.getAt(2).getTopic(), "test-2");
    assertEquals(result.getAt(3).getTopic(), "test-1");
    assertEquals(result.getAt(4).getTopic(), "test-0");


    // Test a topic query
    path = String.format("/api/v1/admin/domains/%s/notifications", TestFactory.DOMAIN_NAME);
    response = request(target(path).queryParam("topic", "test-3")).get();
    assertEquals(response.getStatus(), 200);
    json = response.readEntity(String.class);
    result = objectMapper.readValue(json, QueryResult.class);
    assertEquals(result.getSize(), 1);
    assertEquals(result.getAt(0).getTopic(), "test-3");


    // Test a trait query with a trait value
    path = String.format("/api/v1/admin/domains/%s/notifications", TestFactory.DOMAIN_NAME);
    response = request(target(path).queryParam("traitKey", "even").queryParam("traitValue", "true")).get();
    assertEquals(response.getStatus(), 200);
    json = response.readEntity(String.class);
    result = objectMapper.readValue(json, QueryResult.class);
    assertEquals(result.getSize(), 3);
    assertEquals(result.getAt(0).getTopic(), "test-4");
    assertEquals(result.getAt(1).getTopic(), "test-2");
    assertEquals(result.getAt(2).getTopic(), "test-0");


    // Test a trait query without a trait value
    path = String.format("/api/v1/admin/domains/%s/notifications", TestFactory.DOMAIN_NAME);
    response = request(target(path).queryParam("traitKey", "index")).get();
    assertEquals(response.getStatus(), 200);
    json = response.readEntity(String.class);
    result = objectMapper.readValue(json, QueryResult.class);
    assertEquals(result.getSize(), 8);
    assertEquals(result.getAt(0).getTopic(), "test-7");
    assertEquals(result.getAt(1).getTopic(), "test-6");
    assertEquals(result.getAt(2).getTopic(), "test-5");
    assertEquals(result.getAt(3).getTopic(), "test-4");
    assertEquals(result.getAt(4).getTopic(), "test-3");
    assertEquals(result.getAt(5).getTopic(), "test-2");
    assertEquals(result.getAt(6).getTopic(), "test-1");
    assertEquals(result.getAt(7).getTopic(), "test-0");
  }

  public void test_api_v1_admin_domains_KernelTest_notifications_BadId() {
    String path = String.format("/api/v1/admin/domains/%s/notifications/%s", TestFactory.DOMAIN_NAME, "1");
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 404);
  }

  public void test_api_v1_admin_domains_KernelTest_notifications_GoodId() {

    NotificationRef ref = getNotificationKernel().createNotification(new CreateNotification(
        "unit-test", "Testing 123: " + ReflectUtils.getMethodName(0),
        null, ZonedDateTime.now(), null, Collections.emptyList(), Collections.emptyMap()));

    String path = String.format("/api/v1/admin/domains/%s/notifications/%s", TestFactory.DOMAIN_NAME, ref.getNotificationId());
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v1_admin_domains_KernelTest_routecatalog() {
    String path = String.format("/api/v1/admin/domains/%s/route-catalog", TestFactory.DOMAIN_NAME);
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v1_admin_domains_KernelTest_requests() throws Exception {
    String path = String.format("/api/v1/admin/domains/%s/requests", TestFactory.DOMAIN_NAME);
    Response response = request(target(path)).get();

    assertEquals(response.getStatus(), 200);

    String json = response.readEntity(String.class);
    QueryResult<NotificationRequest> result = objectMapper.readValue(json, QueryResult.class);

    assertNotNull(result);
  }
}