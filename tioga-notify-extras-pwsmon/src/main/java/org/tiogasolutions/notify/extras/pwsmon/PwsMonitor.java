package org.tiogasolutions.notify.extras.pwsmon;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;
import org.tiogasolutions.runners.grizzly.spring.ApplicationResolver;
import org.tiogasolutions.runners.grizzly.spring.GrizzlySpringServer;
import org.tiogasolutions.runners.grizzly.spring.ServerConfigResolver;

import java.nio.file.Path;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class PwsMonitor {

  private static final Logger log = getLogger(PwsMonitor.class);

  public static void main(String...args) throws Exception {
    // Priority #1, configure default logging levels. This will be
    // overridden later when/if the logback.xml is found and loaded.
    AppUtils.initLogback(Level.WARN);

    // Assume we want by default INFO on when & how the grizzly server
    // is started. Possibly overwritten by logback.xml if used.
    AppUtils.setLogLevel(Level.INFO, PwsMonitor.class);
    AppUtils.setLogLevel(Level.INFO, GrizzlySpringServer.GRIZZLY_CLASSES);

    // Load the resolver which gives us common tools for identifying
    // the runtime & config directories, logback.xml, etc.
    AppPathResolver resolver = new AppPathResolver("pws.");
    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    // Re-init logback if we can find the logback.xml
    Path logbackFile = AppUtils.initLogback(configDir, "pws.log.config", "logback.xml");

    // Locate the spring file for this app.
    String springConfigPath = resolver.resolveSpringPath(configDir, "spring-pws-config.xml", "classpath:/tioga-notify-extras-pwsmon/spring-pws-config.xml");
    String activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

    boolean shuttingDown = Arrays.asList(args).contains("-shutdown");
    String action = (shuttingDown ? "Shutting down" : "Starting");

    log.info("{} server:\n" +
      "  *  Runtime Dir     (pws.runtime.dir)     {}\n" +
      "  *  Config Dir      (pws.config.dir)      {}\n" +
      "  *  Logback File    (pws.log.config)      {}\n" +
      "  *  Spring Path     (pws.spring.config)   {}\n" +
      "  *  Active Profiles (pws.active.profiles) {}", action, runtimeDir, configDir, logbackFile, springConfigPath, activeProfiles);

    // Create an instance of the grizzly server.
    GrizzlySpringServer grizzlyServer = new GrizzlySpringServer(
      ServerConfigResolver.fromClass(GrizzlyServerConfig.class),
      ApplicationResolver.fromClass(PwsApplication.class),
      activeProfiles,
      springConfigPath
    );

    grizzlyServer.packages("org.tiogasolutions.notify");

    if (shuttingDown) {
      ShutdownUtils.shutdownRemote(grizzlyServer.getConfig());
      log.warn("Shut down server at {}:{}", grizzlyServer.getConfig().getHostName(), grizzlyServer.getConfig().getShutdownPort());
      System.exit(0);
      return;
    }

    // Lastly, start the server.
    grizzlyServer.start();
  }
}
