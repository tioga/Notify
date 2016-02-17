package org.tiogasolutions.notify.extras.pwsmon.pub;

import org.testng.annotations.Test;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;

@Test
public class LoginRequestTest {

  private TiogaJacksonTranslator translator = new TiogaJacksonTranslator();

  public void testTranslation() {
    LoginRequest loginRequest = new LoginRequest("mickey.mouse@disney.com", "some-password");
    String result = translator.toJson(loginRequest);
    System.out.println(result);
  }
}