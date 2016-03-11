package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;

import java.nio.file.Path;
import java.util.Arrays;

import static java.util.Arrays.asList;
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
    AppUtils.setLogLevel(Level.INFO, GrizzlyServer.class);

    // Load the resolver which gives us common tools for identifying
    // the runtime & config directories, logback.xml, etc.
    AppPathResolver resolver = new AppPathResolver("notify.");
    Path runtimeDir = resolver.resolveRuntimePath();
    Path configDir = resolver.resolveConfigDir(runtimeDir);

    // Re-init logback if we can find the logback.xml
    Path logbackFile = AppUtils.initLogback(configDir, "notify.log.config", "logback.xml");

    // Locate the spring file for this app.
    String springConfigPath = resolver.resolveSpringPath(configDir, "classpath:/tioga-notify-server-grizzly/spring-config.xml");
    String[] activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

    boolean shuttingDown = Arrays.asList(args).contains("-shutdown");
    String action = (shuttingDown ? "Shutting down" : "Starting");

    log.info("{} server:\n" +
      "  *  Runtime Dir     (notify.runtime.dir)     {}\n" +
      "  *  Config Dir      (notify.config.dir)      {}\n" +
      "  *  Logback File    (notify.log.config)      {}\n" +
      "  *  Spring Path     (notify.spring.config)   {}\n" +
      "  *  Active Profiles (notify.active.profiles) {}", action, runtimeDir, configDir, logbackFile, springConfigPath, asList(activeProfiles));

    AbstractXmlApplicationContext applicationContext = createXmlConfigApplicationContext(springConfigPath, activeProfiles);

    GrizzlyServer grizzlyServer = applicationContext.getBean(GrizzlyServer.class);

    if (shuttingDown) {
      ShutdownUtils.shutdownRemote(grizzlyServer.getConfig());
      log.warn("Shut down server at {}:{}", grizzlyServer.getConfig().getHostName(), grizzlyServer.getConfig().getShutdownPort());
      System.exit(0);
      return;
    }

    // Lastly, start the server.
    grizzlyServer.start();
  }

  public static AbstractXmlApplicationContext createXmlConfigApplicationContext(String xmlConfigPath, String...activeProfiles) {

    boolean classPath = xmlConfigPath.startsWith("classpath:");
    AbstractXmlApplicationContext applicationContext = classPath ?
      new ClassPathXmlApplicationContext() :
      new FileSystemXmlApplicationContext();

    applicationContext.setConfigLocation(xmlConfigPath);
    applicationContext.getEnvironment().setActiveProfiles(activeProfiles);
    applicationContext.refresh();
    return applicationContext;
  }
}
