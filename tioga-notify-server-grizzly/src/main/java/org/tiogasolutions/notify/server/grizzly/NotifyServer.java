package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;
import org.tiogasolutions.runners.grizzly.spring.ApplicationResolver;
import org.tiogasolutions.runners.grizzly.spring.GrizzlySpringServer;
import org.tiogasolutions.runners.grizzly.spring.ServerConfigResolver;

import java.nio.file.Path;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class NotifyServer {

  private static final Logger log = getLogger(NotifyServer.class);

  public static void start(String...args) throws Exception {
    // Priority #1, configure default logging levels. This will be
    // overridden later when/if the logback.xml is found and loaded.
    AppUtils.initLogback(Level.WARN);

    // Assume we want by default INFO on when & how the grizzly server
    // is started. Possibly overwritten by logback.xml if used.
    AppUtils.setLogLevel(Level.INFO, NotifyServer.class);
    AppUtils.setLogLevel(Level.INFO, GrizzlySpringServer.GRIZZLY_CLASSES);

    // Load the resolver which gives us common tools for identifying
    // the runtime & config directories, logback.xml, etc.
    AppPathResolver resolver = new AppPathResolver("notify.");
    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    // Re-init logback if we can find the logback.xml
    Path logbackFile = AppUtils.initLogback(configDir, "notify.log.config", "logback.xml");

    // Locate the spring file for this app.
    String springConfigPath = resolver.resolveSpringPath(configDir, "classpath:/tioga-notify-server-grizzly/spring-config.xml");
    String activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

    boolean shuttingDown = Arrays.asList(args).contains("-shutdown");
    String action = (shuttingDown ? "Shutting down" : "Starting");

    log.info("{} server:\n" +
      "  *  Runtime Dir     (notify.runtime.dir)     {}\n" +
      "  *  Config Dir      (notify.config.dir)      {}\n" +
      "  *  Logback File    (notify.log.config)      {}\n" +
      "  *  Spring Path     (notify.spring.config)   {}\n" +
      "  *  Active Profiles (notify.active.profiles) {}", action, runtimeDir, configDir, logbackFile, springConfigPath, activeProfiles);

    // Create an instance of the grizzly server.
    GrizzlySpringServer grizzlyServer = new GrizzlySpringServer(
      ServerConfigResolver.fromClass(GrizzlyServerConfig.class),
      ApplicationResolver.fromClass(NotifyApplication.class),
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
