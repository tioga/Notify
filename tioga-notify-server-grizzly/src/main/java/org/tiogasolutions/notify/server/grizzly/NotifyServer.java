package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;
import org.tiogasolutions.runners.grizzly.LoggerFacade;
import org.tiogasolutions.runners.jersey.support.JerseySpringBridge;

import java.nio.file.Path;

public class NotifyServer {

  private static final Logger log = LoggerFactory.getLogger(NotifyServer.class);

  public static void main(String...args) throws Exception {

    String prefix = "notify.";
    AppPathResolver resolver = new AppPathResolver(log::info, prefix);

    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    initializeLogback(configDir, prefix + "log_config", "logback.xml");

    String springConfigPath = resolver.resolveSpringPath(configDir);
    String activeProfiles = resolver.resolveSpringProfiles();

    // Create our application, initializing it with the specified spring file.
    NotifyApplication notifyApp = new NotifyApplication(activeProfiles, springConfigPath);

    // Get from the app an instance of the grizzly server config.
    GrizzlyServerConfig serverConfig = notifyApp.getBeanFactory().getBean(GrizzlyServerConfig.class);

    // Create a facade around Slf4j for the server's initialization routines.
    LoggerFacade loggerFacade = new LoggerFacade() {
      @Override public void info(String message) { log.info(message); }
      @Override public void error(String message, Throwable e) { log.error(message, e); }
    };

    // Create an instance of the grizzly server.
    GrizzlyServer grizzlyServer = new GrizzlyServer(notifyApp, serverConfig, loggerFacade);

    // Before we start it, register a hook for our jersey-spring bridge.
    grizzlyServer.getResourceConfig().register(new JerseySpringBridge(notifyApp.getBeanFactory()));

    // Lastly, start the server.
    grizzlyServer.start();
  }

  private static void initializeLogback(Path configDir, String propertyName, String fileName) {

    // Reroute java.util.Logger to SLF4J
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    String logConfigArg = EnvUtils.findProperty(propertyName, fileName);
    Path logConfigFile = configDir.resolve(logConfigArg);
    log.info("Configure logging from  {}", logConfigFile.toString());

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    try {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(context);

      // Call context.reset() to clear any previous configuration, e.g. default
      // configuration. For multi-step configuration, omit calling context.reset().
      context.reset();
      configurator.doConfigure(logConfigFile.toString());

    } catch (JoranException je) {
      je.printStackTrace();
      // StatusPrinter will handle this
    }

    StatusPrinter.printInCaseOfErrorsOrWarnings(context);
  }
}
