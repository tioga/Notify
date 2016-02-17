package org.tiogasolutions.notify.extras.pwsmon.pub;

import org.testng.annotations.Test;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;

@Test
public class PromptsTest {

  private TiogaJacksonTranslator translator = new TiogaJacksonTranslator();

  public void testTranslation() {
    Prompts prompts = new Prompts("mickey.mouse@disney.com", "some-password");
    String result = translator.toJson(prompts);
    System.out.println(result);
  }

}