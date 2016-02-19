package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class AppInfo {

  private final Map<String,String> environment = new HashMap<String,String>();

  public AppInfo() {
  }

  @JsonProperty("environment_json")
  public Map<String, String> getEnvironment() {
    return environment;
  }
}
