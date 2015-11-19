package org.tiogasolutions.notify.engine.web;

import org.springframework.stereotype.Component;

import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotifyApplication extends Application {

  protected final Map<String, Object> properties = new HashMap<>();

  public NotifyApplication() {
    properties.put("app.admin.context", "/api/v1/admin");
    properties.put("app.client.context", "/api/v1/client");
  }

  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }
}
