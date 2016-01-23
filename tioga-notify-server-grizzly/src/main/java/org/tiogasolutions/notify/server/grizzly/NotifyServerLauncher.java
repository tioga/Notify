package org.tiogasolutions.notify.server.grizzly;

import java.net.URL;

public class NotifyServerLauncher {

  public static void main(String...args) throws Throwable {
    URL location = NotifyServer.class.getProtectionDomain().getCodeSource().getLocation();
    System.out.println("Starting application from " + location);

    if (location.getPath().endsWith(".jar") == false) {
      NotifyServer.start(args);

    } else {
      JarClassLoader jcl = new JarClassLoader();
      jcl.invokeStart(NotifyServer.class.getName(), args);
    }
  }
}
