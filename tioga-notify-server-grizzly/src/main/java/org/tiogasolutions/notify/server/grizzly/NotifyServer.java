package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.LogUtils;
import org.tiogasolutions.lib.spring.jersey.JerseySpringBridge;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class NotifyServer {

  private static final Logger log = getLogger(NotifyServer.class);

  public static void main(String...args) throws Exception {
    List<String> arguments = Arrays.asList(args);

    // Priority #1, configure default logging levels. This will be overridden later
    // when/if the logback.xml is found and loaded.
    LogUtils.initLogback(Level.WARN);
    // Assume we want by default INFO on when & how the grizzly server is started
    ((ch.qos.logback.classic.Logger) getLogger(NotifyServer.class)).setLevel(Level.INFO);
    ((ch.qos.logback.classic.Logger) getLogger(GrizzlyServer.class)).setLevel(Level.INFO);

    // Load the resolver which gives us common tools for identifying the
    // runtime & config directories, logback.xml, etc.
    AppPathResolver resolver = new AppPathResolver(getLogger(AppPathResolver.class)::info, "notify.");

    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    // Re-init logback if we can find the logback.xml
    Path logbackFile = LogUtils.initLogback(configDir, "notify.log.config", "logback.xml");

    // Locate the spring file for this app or use DEFAULT_SPRING_FILE from the classpath if one is not found.
    String springConfigPath = resolver.resolveSpringPath(configDir, null);
    String activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

    log.info("Starting Notify Server:\n" +
      "  *  Runtime Dir:  {}\n" +
      "  *  Config Dir:   {}\n" +
      "  *  Logback File: {}\n" +
      "  *  Spring Path:  {}", runtimeDir, configDir, logbackFile, springConfigPath);

    // Create our application, initializing it with the specified spring file.
    NotifyApplication application = new NotifyApplication(activeProfiles, springConfigPath);

    // Get from the app an instance of the grizzly server config.
    GrizzlyServerConfig serverConfig = application.getBeanFactory().getBean(GrizzlyServerConfig.class);

    // Create an instance of the grizzly server.
    GrizzlyServer grizzlyServer = new GrizzlyServer(serverConfig, application);

    if (arguments.contains("-shutdown")) {
      GrizzlyServer.shutdownRemote(serverConfig.getHostName(), serverConfig.getShutdownPort());
      String msg = String.format("Shutting down Notify Server at %s:%s", serverConfig.getHostName(), serverConfig.getShutdownPort());
      log.warn(msg);
      System.exit(0);
      return;
    }

    // Before we start it, register a hook for our jersey-spring bridge.
    JerseySpringBridge jerseySpringBridge = new JerseySpringBridge(application.getBeanFactory());
    grizzlyServer.getResourceConfig().register(jerseySpringBridge);

    // Lastly, start the server.
    grizzlyServer.start();
  }
}
