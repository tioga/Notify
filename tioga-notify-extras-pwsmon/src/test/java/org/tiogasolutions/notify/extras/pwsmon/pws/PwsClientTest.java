package org.tiogasolutions.notify.extras.pwsmon.pws;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tiogasolutions.notify.extras.pwsmon.pws.pub.*;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Test
public class PwsClientTest {

  private PwsClient pwsClient;

  @BeforeClass
  public void beforeClass() {
    pwsClient = new PwsClient();
    pwsClient.setIgnoringCertificates(true);

    LoginResponse response = pwsClient.login("me@jacobparr.com", "go2Pivotal");
    Assert.assertNotNull(response);
  }

  public void testGetEvents() throws Exception {
    GetEventsResponse response = pwsClient.getApplicationEvents(0);
    Assert.assertNotNull(response);

    SortedSet<Event> events = response.getAppEvents("tioga-notify-engine");
    for (Event event : events) {
      String date = event.getTimestamp().toLocalDateTime().toString().replace("T", "");
      System.out.printf("%-20s %s\n", date, event.getType());
      if (event.getMetadata().getExitDescription() != null) {
        String desc = event.getMetadata().getExitDescription();
        desc = desc.replace("\n\n", "\n");
        System.out.printf("* %s\n\n", desc);
      }
    }
  }


  public void testGetApplications() throws Exception {
    GetAppsResponse response = pwsClient.getApplications();
    Assert.assertNotNull(response);

    List<AppResource> resources = response.getAppResources();
    for (AppResource appResource : resources) {
      App app = appResource.getApp();
      System.out.printf("%s: %s\n", app.getName(), appResource.getMetadata().getGuid());
    }
  }
}
