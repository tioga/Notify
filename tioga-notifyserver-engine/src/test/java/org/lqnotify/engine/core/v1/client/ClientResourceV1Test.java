package org.lqnotify.engine.core.v1.client;

import org.tiogasolutions.dev.common.ReflectUtils;
import org.lqnotify.engine.core.AbstractEngineJaxRsTest;
import org.lqnotify.kernel.notification.CreateNotification;
import org.lqnotify.pub.DomainProfile;
import org.lqnotify.pub.NotificationRef;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.time.ZonedDateTime;
import java.util.Collections;

import static java.lang.String.format;
import static org.lqnotify.kernel.TestFactory.DOMAIN_NAME;
import static org.testng.Assert.assertEquals;

@Test
public class ClientResourceV1Test extends AbstractEngineJaxRsTest {

  private String apiKey;
  private String apiPass;
  
  @BeforeMethod
  public void beforeClass() throws Exception {
    DomainProfile domainProfile = getDomainKernel().getOrCreateDomain(DOMAIN_NAME);
    apiKey = domainProfile.getApiKey();
    apiPass = domainProfile.getApiPassword();
    getExecutionManager().newApiContext(apiKey);
  }

  @AfterMethod
  public void afterClass() throws Exception {
    getExecutionManager().clearContext();
  }

  public void test_api_v1_client_$NoAuthorization() {
    String path = format("/api/v1/client");
    Response response = target(path).request().get();

    assertEquals(response.getStatus(), 401);
  }

  public void test_api_v1_client_$BadAuthorization() {
    String path = format("/api/v1/client");
    Response response = target(path).request().header("Authorization", toHttpAuth("bad","guy")).get();

    assertEquals(response.getStatus(), 401);
  }

  public void test_api_v1_client() {
    String path = format("/api/v1/client");
    Response  response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
  }

  public void test_api_v1_client_notifications() {
    String path = format("/api/v1/client/notifications");
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
    System.out.println(response.getEntity());
  }

  public void test_api_v1_client_notifications_BadId() {
    String path = format("/api/v1/client/notifications/%s", 1);
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 404);
    System.out.println(response.getEntity());
  }

  public void test_api_v1_client_notifications_GoodId() {

    NotificationRef ref = getNotificationKernel().createNotification(new CreateNotification(
        "unit-test", "Testing 123: " + ReflectUtils.getMethodName(0),
        null, ZonedDateTime.now(), null, Collections.emptyMap()));

    String path = format("/api/v1/client/notifications/%s", ref.getNotificationId());
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
    System.out.println(response.getEntity());
  }

  public void test_api_v1_client_routecatalog() {
    String path = format("/api/v1/client/route-catalog");
    Response response = target(path).request().header("Authorization", toHttpAuth(apiKey, apiPass)).get();

    assertEquals(response.getStatus(), 200);
    System.out.println(response.getEntity());
  }
}