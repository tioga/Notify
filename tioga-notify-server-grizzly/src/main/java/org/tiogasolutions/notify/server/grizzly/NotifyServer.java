package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.runner.jersey.support.JerseySpringBridge;
import org.tiogasolutions.runner.jersey.support.ResourceConfigAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NotifyServer {
  private static final Logger log = LoggerFactory.getLogger(NotifyServer.class);

  public static void main(String...args) {

    // Identify runtimeDir from system property or program argument
    String runtimeDirArg = EnvUtils.findProperty("notify.runtime_dir");
    if (runtimeDirArg == null) {
      // Look for a directory named "runtime" near the current directory -- useful for development
      File currentDir = IoUtils.currentDir();
      File moduleDir = IoUtils.findDirNear(currentDir, "runtime");
      if (moduleDir == null) {
        throw new RuntimeException("Application runtime directory not specified and could not be found automatically, must be provided with system property notify.runtime_dir.");
      }
      runtimeDirArg = moduleDir.getAbsolutePath();
    }
    Path runtimeDir = Paths.get(runtimeDirArg).toAbsolutePath();

    // Verify the runtimeDir
    if (Files.notExists(runtimeDir)) {
      throw new RuntimeException("Runtime directory " + runtimeDir + " does not exist");
    } else if (!Files.isDirectory(runtimeDir)) {
      throw new RuntimeException("Runtime directory " + runtimeDir + " is not a directory");
    }
    System.out.println("Runtime dir: " + runtimeDir);

    // Ensure the runtime_dir property is always set.
    System.setProperty("notify.runtime_dir", runtimeDir.toString());

    // Verify config dir.
    Path configDir = runtimeDir.resolve("config");
    if (Files.notExists(configDir)) {
      throw new RuntimeException("Config directory " + configDir + " does not exist");
    } else if (!Files.isDirectory(runtimeDir)) {
      throw new RuntimeException("Config directory " + configDir + " is not a directory");
    }
    System.out.println("Config dir: " + configDir);

    // Always set notify.runtime_dir to absolute
    System.setProperty("notify.runtime_dir", runtimeDir.toString());

    // Initialize logging.
    String logConfigArg = EnvUtils.findProperty("notify.log_config", "logback.xml");
    Path logConfigFile = configDir.resolve(logConfigArg);
    System.out.println("Configure logging from: " + logConfigFile.toString());
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

    // Include Spring profile "hosted" (system property seems best way to do this in WebServer while using Jersey)
    String activeProfiles = System.getProperty("spring.profiles.active");
    activeProfiles = (activeProfiles != null) ? activeProfiles + ",hosted" : "hosted";
    System.setProperty("spring.profiles.active", activeProfiles);
    log.info("Active spring profiles: " + activeProfiles);

    // Initialize Spring
    String springFileName = EnvUtils.findProperty("notify.spring_config", "spring-config.xml");
    Path springConfig = configDir.resolve(springFileName);
    String springConfigPath = springConfig.toUri().toString();
    NotifyJaxRsConfig jaxRsConfig = new NotifyJaxRsConfig(activeProfiles, springConfigPath);
    ResourceConfigAdapter adapter = new ResourceConfigAdapter(jaxRsConfig);
    adapter.register(new JerseySpringBridge(jaxRsConfig.getBeanFactory()));
    GrizzlyServerConfig serverConfig = jaxRsConfig.getBeanFactory().getBean(GrizzlyServerConfig.class);

    // Startup Grizzly
    GrizzlyServer grizzlyServer = new GrizzlyServer(serverConfig);
    grizzlyServer.start(adapter);

  }

}
