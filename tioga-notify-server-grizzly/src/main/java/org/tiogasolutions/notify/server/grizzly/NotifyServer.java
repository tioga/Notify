package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.LogUtils;
import org.tiogasolutions.lib.spring.jersey.JerseySpringBridge;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;
import org.tiogasolutions.runners.grizzly.LoggerFacade;

import java.nio.file.Path;

public class NotifyServer {

  private static final Logger log = LoggerFactory.getLogger(NotifyServer.class);

  public static final String DEFAULT_SPRING_FILE = "/tioga-notify-server-grizzly/spring-config.xml";

  public static void main(String...args) throws Exception {

    LogUtils.initLogback(Level.WARN);

    AppPathResolver resolver = new AppPathResolver(log::info, "notify.");
    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    LogUtils.initLogback(configDir, "notify.log.config", "logback.xml");

    String springConfigPath = resolver.resolveSpringPath(configDir, "classpath:"+DEFAULT_SPRING_FILE);
    String activeProfiles = resolver.resolveSpringProfiles();

    // Create our application, initializing it with the specified spring file.
    NotifyApplication notifyApp = new NotifyApplication(activeProfiles, springConfigPath);

    // Get from the app an instance of the grizzly server config.
    GrizzlyServerConfig serverConfig = notifyApp.getBeanFactory().getBean(GrizzlyServerConfig.class);

    // Create a facade around Slf4j for the server's initialization routines.
    LoggerFacade loggerFacade = new LoggerFacade() {
      @Override public void info(String message) { log.info(message); }
      @Override public void warn(String message) { log.warn(message); }
      @Override public void error(String message, Throwable e) { log.error(message, e); }
    };

    // Create an instance of the grizzly server.
    GrizzlyServer grizzlyServer = new GrizzlyServer(notifyApp, serverConfig, loggerFacade);

    // Before we start it, register a hook for our jersey-spring bridge.
    JerseySpringBridge jerseySpringBridge = new JerseySpringBridge(notifyApp.getBeanFactory());
    grizzlyServer.getResourceConfig().register(jerseySpringBridge);

    // Lastly, start the server.
    grizzlyServer.start();
  }
}
