package org.tiogasolutions.notify.extras.pwsmon;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.dev.common.EnvUtils;

import static org.slf4j.LoggerFactory.getLogger;

public class PwsMonitor {

  private static final Logger log = getLogger(PwsMonitor.class);

  public static void main(String...args) {
    // Priority #1, configure default logging levels. This will be
    // overridden later when/if the logback.xml is found and loaded.
    AppUtils.initLogback(Level.WARN);

    // Assume we want by default INFO on when & how the grizzly server
    // is started. Possibly overwritten by logback.xml if used.
    AppUtils.setLogLevel(Level.INFO, PwsMonitor.class);

    try {
      new PwsMonitor().run();

    } catch (Exception e) {
      e.printStackTrace();
      System.err.flush();
    }
    System.err.flush();
    System.out.flush();
    System.exit(0);
  }

  private void run() throws Exception {
    String value = EnvUtils.requireProperty("delay");
    long delay = Long.valueOf(value);


    Thread.sleep(delay);
  }
}
