package org.tiogasolutions.notify.extras.pwsmon.pws;

import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.lib.jaxrs.client.BasicAuthorization;
import org.tiogasolutions.lib.jaxrs.client.BearerAuthorization;
import org.tiogasolutions.lib.jaxrs.client.SimpleRestClient;
import org.tiogasolutions.notify.extras.pwsmon.pws.pub.Event;
import org.tiogasolutions.notify.extras.pwsmon.pws.pub.GetAppsResponse;
import org.tiogasolutions.notify.extras.pwsmon.pws.pub.GetEventsResponse;
import org.tiogasolutions.notify.extras.pwsmon.pws.pub.LoginResponse;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.SortedSet;

public class PwsClient {

  private static final String clientId = "lkj32dlj2398edk23o4sk";

  private TiogaJacksonTranslator translator = new TiogaJacksonTranslator();

  private SimpleRestClient loginClient = new SimpleRestClient(translator, "https://login.run.pivotal.io", new BasicAuthorization("cf",""));
  private SimpleRestClient apiClient = new SimpleRestClient(translator, "https://api.run.pivotal.io");

  private String refreshToken;

  public PwsClient() {
  }

  public void setIgnoringCertificates(boolean ignoringCertificates) {
    apiClient.setIgnoringCertificates(ignoringCertificates);
    loginClient.setIgnoringCertificates(ignoringCertificates);
  }

  public LoginResponse login(String emailAddress, String password) {
    Form form = new Form();
    form.param("grant_type", "password");
    form.param("username", emailAddress);
    form.param("password", password);

    LoginResponse response = loginClient.post(LoginResponse.class, "/oauth/token", form);

    this.refreshToken = response.getRefreshToken();
    String accessToken = response.getAccessToken();
    apiClient.setAuthorization(new BearerAuthorization(accessToken));

    return response;
  }

  public SortedSet<Event> getApplicatinEvents(String appName, int max) {
    GetEventsResponse response = getApplicationEvents(max);
    return response.getAppEvents(appName);
  }

  public GetEventsResponse getApplicationEvents(int max) {
    if (max < 1) max = 100;
    max = Math.min(100, max);
    String resultsPerPage = "results-per-page="+max;
    return apiClient.get(GetEventsResponse.class, "/v2/events", resultsPerPage);
  }

  public GetAppsResponse getApplications() {
    String content = apiClient.get(String.class, "/v2/apps");
    return apiClient.translateResponse(GetAppsResponse.class, content);
  }
}
