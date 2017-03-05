package org.tiogasolutions.notify.engine.v2.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.notify.engine.AbstractEngineJaxRsTest;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.test.TestFactory;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.NotificationRef;

import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.Collections;

import static java.lang.String.format;
import static org.testng.Assert.assertEquals;

@Test
public class ClientResourceV2Test extends AbstractEngineJaxRsTest {

  private String apiKey;
  private String apiPass;

  @Autowired
  private DomainKernel domainKernel;

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  NotificationKernel notificationKernel;

  @BeforeMethod
  public void beforeClass() throws Exception {
    DomainProfile domainProfile = domainKernel.getOrCreateDomain(TestFactory.DOMAIN_NAME);
    apiKey = domainProfile.getApiKey();
    apiPass = domainProfile.getApiPassword();
    executionManager.newApiContext(apiKey);
  }

  @AfterMethod
  public void afterClass() throws Exception {
    executionManager.clearContext();
  }

  public void test_api_v2_client_$NoAuthorization() {
    String path = "/api/v2/client";
    Response response = target(path).request().get();

    assertEquals(response.getStatus(), 401);
  }

  public void test_api_v2_client_$BadAuthorization() {
    String path = "/api/v2/client";
    Response response = target(path).request().header("Authorization", toHttpAuth("bad","guy")).get();

    assertEquals(response.getStatus(), 401);
  }

  public void test_api_v2_client() {
    String path = "/api/v2";
    Response  response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v2_client_notifications() {
    String path = "/api/v2/notifications";
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v2_client_notifications_BadId() {
    String path = format("/api/v2/client/notifications/%s", 1);
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 404);
  }

  public void test_api_v2_client_notifications_GoodId() {

    NotificationRef ref = notificationKernel.createNotification(new CreateNotification(
        "unit-test", "Testing 123: " + ReflectUtils.getMethodName(0),
        null, ZonedDateTime.now(), null, Collections.emptyList(), Collections.emptyMap()));

    String path = format("/api/v2/notifications/%s", ref.getNotificationId());
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v2_client_routecatalog() {
    String path = "/api/v2/route-catalog";
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
  }
}