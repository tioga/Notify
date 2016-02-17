package org.tiogasolutions.notify.extras.pwsmon.pub;

import java.time.ZonedDateTime;

public class LoginRequest {

  private final ZonedDateTime timestamp;
  private final Prompts prompts;

  public LoginRequest(String emailAddress, String password) {
    this.timestamp = ZonedDateTime.now();
    this.prompts = new Prompts(emailAddress, password);
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public Prompts getPrompts() {
    return prompts;
  }
}
