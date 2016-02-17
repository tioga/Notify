package org.tiogasolutions.notify.extras.pwsmon.pws;

import org.tiogasolutions.dev.common.exceptions.ApiUnauthorizedException;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.lib.jaxrs.client.SimpleRestClient;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.Map;

public class PwsClient {

  private static final String clientId = "lkj32dlj2398edk23o4sk";

  private TiogaJacksonTranslator translator = new TiogaJacksonTranslator();

  private SimpleRestClient loginClient = new SimpleRestClient(translator, "https://login.run.pivotal.io");
  private SimpleRestClient uaaClient = new SimpleRestClient(translator, "https://uaa.run.pivotal.io");

  private SimpleRestClient apiClient = new SimpleRestClient(translator, "https://api.run.pivotal.io") {
    public <T> T get(Class<T> returnType, String subUrl, Map<String, Object> queryMap, String...acceptedResponseTypes) {

      Invocation.Builder builder = builder(subUrl, queryMap, acceptedResponseTypes);

      builder.header("Authorization", "bearer eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJlNGQ0NDI4My1mMWU1LTQ1ZWItOGQyMy03NDRhZjhlMDczNTIiLCJzdWIiOiI2YzgxMzZiMi1iYzkzLTQ0MzItYTFjYS1mNDcyNzM3OTkyMjkiLCJzY29wZSI6WyJjbG91ZF9jb250cm9sbGVyLnJlYWQiLCJwYXNzd29yZC53cml0ZSIsImNsb3VkX2NvbnRyb2xsZXIud3JpdGUiLCJvcGVuaWQiLCJ1YWEudXNlciJdLCJjbGllbnRfaWQiOiJjZiIsImNpZCI6ImNmIiwiYXpwIjoiY2YiLCJncmFudF90eXBlIjoicGFzc3dvcmQiLCJ1c2VyX2lkIjoiNmM4MTM2YjItYmM5My00NDMyLWExY2EtZjQ3MjczNzk5MjI5Iiwib3JpZ2luIjoidWFhIiwidXNlcl9uYW1lIjoibWVAamFjb2JwYXJyLmNvbSIsImVtYWlsIjoibWVAamFjb2JwYXJyLmNvbSIsInJldl9zaWciOiI3YjRiNjYzMyIsImlhdCI6MTQ1NTY4ODU0OCwiZXhwIjoxNDU1Njg5MTQ4LCJpc3MiOiJodHRwczovL3VhYS5ydW4ucGl2b3RhbC5pby9vYXV0aC90b2tlbiIsInppZCI6InVhYSIsImF1ZCI6WyJjbG91ZF9jb250cm9sbGVyIiwicGFzc3dvcmQiLCJjZiIsInVhYSIsIm9wZW5pZCJdfQ.kLwrsDIS4ZTJm8ptjkRhrqaE0_qjkwrObDez59fb6wLEdEV840uHNnZwQGtgFht_NnSFWkxg8otecmDvw4tqN9oiIIJX7UaJs3l9s7RCvuLY7ndi7MpGIibNyWKKQ5vQ1770ZXYoSL_ykcevw8Cjd6a3KSVnr369mOywKyOvBfaAi5JlHDdr6RbcaYUw0XGa0iHqn_JZiOGn45JRJ7_OR_ZwaLJILWwO1Ue3BQrwpG9RfPIovm3IMotXAb_mXvaNYD2kukBnqHJnELNcIWJW59IAEnYo4gyughxz3DRw1NU-N6pZeuNzS3kU8RJc-Tu25KjPtcv6gORNaJmvst4u4w");
      builder.header("Host", "api.run.pivotal.io");

      Response response = builder.get(Response.class);
      return translateResponse(returnType, response);
    }
  };

  public PwsClient() {
  }

  public String login(String emailAddress, String password) {
    Form form = new Form();
    form.param("grant_type", "password");
    form.param("username", emailAddress);
    form.param("password", password);
    form.param("client_id", clientId);

    return uaaClient.post(String.class, "/token", form);

    //  POST https://api.oauth2server.com/token
    //    grant_type=password&
    //    username=USERNAME&
    //    password=PASSWORD&
    //    client_id=CLIENT_ID
  }

  public String getApplicatinEvents() {
    try {
      return apiClient.get(String.class, "/v2/events", "results-per-page=100", "order-direction=desc");

    } catch (ApiUnauthorizedException e) {

    }

    return null;
  }
}
