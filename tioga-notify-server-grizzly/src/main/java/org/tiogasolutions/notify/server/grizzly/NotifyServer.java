package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.lib.jaxrs.TiogaJaxRsExceptionMapper;
import org.tiogasolutions.lib.jaxrs.jackson.TiogaReaderWriterProvider;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;
import org.tiogasolutions.runners.grizzly.spring.ApplicationResolver;
import org.tiogasolutions.runners.grizzly.spring.GrizzlySpringServer;
import org.tiogasolutions.runners.grizzly.spring.ServerConfigResolver;

import java.nio.file.Path;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class NotifyServer {

  private static final Logger log = getLogger(NotifyServer.class);

  public static void main(String...args) throws Exception {

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
    String springConfigPath = resolver.resolveSpringPath(configDir, null);
    String activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

    log.info("Starting server:\n" +
      "  *  Runtime Dir:  {}\n" +
      "  *  Config Dir:   {}\n" +
      "  *  Logback File: {}\n" +
      "  *  Spring Path ({}):  {}", runtimeDir, configDir, logbackFile, activeProfiles, springConfigPath);

    // Create an instance of the grizzly server.
    GrizzlySpringServer grizzlyServer = new GrizzlySpringServer(
      ServerConfigResolver.fromClass(GrizzlyServerConfig.class),
      ApplicationResolver.fromClass(NotifyApplication.class),
      activeProfiles,
      springConfigPath
    );

    grizzlyServer.packages("org.tiogasolutions.notify");

    if (Arrays.asList(args).contains("-shutdown")) {
      GrizzlyServer.shutdownRemote(grizzlyServer.getConfig());
      log.warn("Shutting down server at {}:{}", grizzlyServer.getConfig().getHostName(), grizzlyServer.getConfig().getShutdownPort());
      System.exit(0);
      return;
    }

    // Lastly, start the server.
    grizzlyServer.start();
  }
}
