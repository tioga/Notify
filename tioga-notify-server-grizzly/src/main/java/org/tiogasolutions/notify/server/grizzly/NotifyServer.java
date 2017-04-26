package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.tiogasolutions.app.common.AppPathResolver;
import org.tiogasolutions.app.common.AppUtils;
import org.tiogasolutions.dev.common.LogbackUtils;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.lib.spring.SpringUtils;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;
import org.tiogasolutions.runners.grizzly.ShutdownUtils;

import java.nio.file.Path;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.tiogasolutions.dev.common.EnvUtils.findProperty;

public class NotifyServer {

    private static final String PREFIX = "notify";
    private static final String APP_NAME = "Notify Engine";

    private static final Logger log = getLogger(NotifyServer.class);

    public static void main(String... args) throws Exception {

        // Priority #1, configure default logging levels. This will be
        // overridden later when/if the logback.xml is found and loaded.
        Level level = Level.WARN;
        AppUtils.initLogback(level);

        String levelCode = findProperty(PREFIX + "_log_level", level.toString());
        level = Level.toLevel(levelCode, null);

        if (StringUtils.isNotBlank(level)) {
            LogbackUtils.setRootLevel(level);
        } else {
            level = Level.WARN;
            log.error("Cannot initialize default log level to \"" + levelCode + "\"");
        }

        // Assume we want by default INFO on when & how the grizzly server
        // is started. Possibly overwritten by logback.xml if used.
        AppUtils.setLogLevel(Level.INFO, NotifyServer.class);
        AppUtils.setLogLevel(Level.INFO, GrizzlyServer.class);

        // Load the resolver which gives us common tools for identifying the
        // runtime & config directories, logback.xml, etc.
        AppPathResolver resolver = new AppPathResolver(PREFIX + "_");
        Path runtimeDir = resolver.resolveRuntimePath();
        Path configDir = resolver.resolveConfigDir(runtimeDir);

        // Re-init logback if we can find the logback.xml
        Path logbackFile = AppUtils.initLogback(level, configDir, PREFIX + "_log_config", "logback.xml");

        // Locate the spring file for this app or use DEFAULT_SPRING_FILE from the classpath if one is not found.
        String springConfigPath = resolver.resolveSpringPath(configDir, format("classpath:/tioga-%s-server-grizzly/spring-config.xml", PREFIX));
        String[] activeProfiles = resolver.resolveSpringProfiles(); // defaults to "hosted"

        AbstractXmlApplicationContext applicationContext = SpringUtils.createXmlConfigApplicationContext(springConfigPath, activeProfiles);
        GrizzlyServer grizzlyServer = applicationContext.getBean(GrizzlyServer.class);
        Notifier notifier = applicationContext.getBean(Notifier.class);

        if (asList(args).contains("-shutdown")) {
            String msg = format("Shutting down %s at %s:%s",
                    APP_NAME,
                    grizzlyServer.getConfig().getHostName(),
                    grizzlyServer.getConfig().getShutdownPort());

            log.warn(msg);
            notifier.begin().summary(msg).trait("action", "shutdown").send().get();

            ShutdownUtils.shutdownRemote(grizzlyServer.getConfig());
            System.exit(0);

        } else {
            String logMessage = format("Starting %s:\n" +
                            "  *  Runtime Dir     (%s_runtime_dir)     %s\n" +
                            "  *  Config Dir      (%s_config_dir)      %s\n" +
                            "  *  Logback File    (%s_log_config)      %s\n" +
                            "  *  Spring Path     (%s_spring_config)   %s\n" +
                            "  *  Active Profiles (%s_active_profiles) %s",
                    APP_NAME,
                    PREFIX, runtimeDir,
                    PREFIX, configDir,
                    PREFIX, logbackFile,
                    PREFIX, springConfigPath,
                    PREFIX, asList(activeProfiles));
            log.info(logMessage);


            GrizzlyServerConfig grizzlyServerConfig = applicationContext.getBean(GrizzlyServerConfig.class);
            logMessage = format("Server config:\n" +
                            "  *  Host Name: %s\n" +
                            "  *  Port: %s\n" +
                            "  *  Shutdown Port: %s\n" +
                            "  *  Context: %s\n" +
                            "  *  Shutdown Timeout: %s",
                    grizzlyServerConfig.getHostName(),
                    grizzlyServerConfig.getPort(),
                    grizzlyServerConfig.getShutdownPort(),
                    grizzlyServerConfig.getContext(),
                    grizzlyServerConfig.getShutdownTimeout());
            log.info(logMessage);


            CouchServersConfig couchServersConfig = applicationContext.getBean(CouchServersConfig.class);
            logMessage = format("Database config:\n" +
                            "  * Master Url: %s" +
                            "  * Master Database Name: %s" +

                            "  * Notification Url: %s" +
                            "  * Notification Database Prefix: %s" +
                            "  * Notification Database Suffix: %s" +

                            "  * Request Url: %s" +
                            "  * Request Database Prefix: %s" +
                            "  * Request Database Suffix: %s",
                    couchServersConfig.getMasterUrl(),
                    couchServersConfig.getMasterDatabaseName(),
                    couchServersConfig.getNotificationUrl(),
                    couchServersConfig.getNotificationDatabasePrefix(),
                    couchServersConfig.getNotificationDatabaseSuffix(),
                    couchServersConfig.getRequestUrl(),
                    couchServersConfig.getRequestDatabasePrefix(),
                    couchServersConfig.getRequestDatabaseSuffix()

            );
            log.info(logMessage);


            notifier.begin().summary("Starting " + APP_NAME)
                    .trait("action", "startup")
                    // application
                    .trait(PREFIX + "_runtime_dir", runtimeDir)
                    .trait(PREFIX + "_config_dir", configDir)
                    .trait(PREFIX + "_log_config", logbackFile)
                    .trait(PREFIX + "_spring_config", springConfigPath)
                    .trait(PREFIX + "_active_profiles", asList(activeProfiles))
                    // server
                    .trait("engine-host-name", grizzlyServerConfig.getHostName())
                    .trait("engine-port", grizzlyServerConfig.getPort())
                    .trait("engine-shutdown-port", grizzlyServerConfig.getShutdownPort())
                    .trait("engine-context", grizzlyServerConfig.getContext())
                    .trait("engine-shutdown-timeout", grizzlyServerConfig.getShutdownTimeout())
                    // database
                    .trait("couch-db-master-url", couchServersConfig.getMasterUrl())
                    .trait("couch-db-master-database-name", couchServersConfig.getMasterDatabaseName())
                    .trait("couch-db-notification-url", couchServersConfig.getNotificationUrl())
                    .trait("couch-db-notification-database-prefix", couchServersConfig.getNotificationDatabasePrefix())
                    .trait("couch-db-notification-database-suffix", couchServersConfig.getNotificationDatabaseSuffix())
                    .trait("couch-db-request-url", couchServersConfig.getRequestUrl())
                    .trait("couch-db-request-database-prefix", couchServersConfig.getRequestDatabasePrefix())
                    .trait("couch-db-request-database-suffix", couchServersConfig.getRequestDatabaseSuffix())
                    // send it
                    .send().get();

            // Lastly, start the server.
            grizzlyServer.start();
        }
    }
}
