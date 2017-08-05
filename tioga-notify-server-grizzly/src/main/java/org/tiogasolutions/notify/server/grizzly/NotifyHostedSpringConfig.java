package org.tiogasolutions.notify.server.grizzly;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.NotifyObjectMapper;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.notify.engine.web.readers.BundledStaticContentReader;
import org.tiogasolutions.notify.kernel.config.CouchEnvironment;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;
import org.tiogasolutions.notify.kernel.config.SystemConfiguration;
import org.tiogasolutions.notify.kernel.config.TrustedUserStore;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;
import org.tiogasolutions.notify.notifier.send.NotificationSender;
import org.tiogasolutions.notify.processor.logger.LoggerTaskProcessor;
import org.tiogasolutions.notify.processor.push.LivePushClientFactory;
import org.tiogasolutions.notify.processor.push.PushTaskProcessor;
import org.tiogasolutions.notify.processor.slack.SlackTaskProcessor;
import org.tiogasolutions.notify.processor.smtp.SmtpTaskProcessor;
import org.tiogasolutions.notify.processor.swing.SwingTaskProcessor;
import org.tiogasolutions.notify.sender.couch.CouchNotificationSender;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.net.UnknownHostException;
import java.util.Arrays;

import static org.tiogasolutions.dev.common.EnvUtils.findProperty;

@Profile("hosted")
@Configuration
public class NotifyHostedSpringConfig {

    @Bean
    public LivePushClientFactory livePushClientFactory() {
        return new LivePushClientFactory();
    }

    @Bean
    public NotifyObjectMapper notifyObjectMapper() {
        return new NotifyObjectMapper();
    }

    @Bean
    public TiogaJacksonTranslator tiogaJacksonTranslator(NotifyObjectMapper objectMapper) {
        return new TiogaJacksonTranslator(objectMapper);
    }

    @Bean
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public TaskProcessorExecutor taskProcessorExecutor(
            DomainKernel domainKernel, EventBus eventBus,
            SwingTaskProcessor swingTaskProcessor,
            LoggerTaskProcessor loggerTaskProcessor,
            PushTaskProcessor pushTaskProcessor,
            SlackTaskProcessor slackTaskProcessor,
            SmtpTaskProcessor smtpTaskProcessor) {

        return new TaskProcessorExecutor(domainKernel, eventBus, Arrays.asList(
                swingTaskProcessor,
                loggerTaskProcessor,
                pushTaskProcessor,
                slackTaskProcessor,
                smtpTaskProcessor
        ));
    }

    @Bean(name = "org.tiogasolutions.notify.kernel.config.SystemConfiguration")
    public SystemConfiguration systemConfiguration() {
        return new SystemConfiguration("*", false);
    }

    @Bean(name = "org.tiogasolutions.notify.engine.web.readers.ExternalizedStaticContentReader")
    BundledStaticContentReader bundledStaticContentReader() {
        return new BundledStaticContentReader("/tioga-notifier-engine");
    }

    @Bean(name = "org.tiogasolutions.runners.grizzly.GrizzlyServerConfig")
    public GrizzlyServerConfig grizzlyServerConfig(@Value("${notify_context}") String notifyContext) {
        GrizzlyServerConfig config = new GrizzlyServerConfig();
        config.setHostName(findProperty("notify_hostName", "0.0.0.0"));
        config.setPort(Integer.valueOf(findProperty("notify_port", "8080")));
        config.setShutdownPort(Integer.valueOf(findProperty("notify_shutdownPort", "8081")));
        config.setContext(notifyContext);
        config.setToOpenBrowser(false);
        return config;
    }

    @Bean(name = "org.tiogasolutions.notify.kernel.config.CouchServersConfig")
    public CouchServersConfig couchServersConfig(@Value("${notify_masterUrl}") String masterUrl,
                                                 @Value("${notify_masterUsername}") String masterUsername,
                                                 @Value("${notify_masterPassword}") String masterPassword,
                                                 @Value("${notify_masterDatabaseName}") String masterDatabaseName,

                                                 @Value("${notify_notificationUrl}") String notificationUrl,
                                                 @Value("${notify_notificationUsername}") String notificationUsername,
                                                 @Value("${notify_notificationPassword}") String notificationPassword,
                                                 @Value("${notify_notificationDatabasePrefix}") String notificationDatabasePrefix,
                                                 @Value("${notify_notificationDatabaseSuffix}") String notificationDatabaseSuffix,

                                                 @Value("${notify_requestUrl}") String requestUrl,
                                                 @Value("${notify_requestUsername}") String requestUsername,
                                                 @Value("${notify_requestPassword}") String requestPassword,
                                                 @Value("${notify_requestDatabasePrefix}") String requestDatabasePrefix,
                                                 @Value("${notify_requestDatabaseSuffix}") String requestDatabaseSuffix) {

        CouchServersConfig config = new CouchServersConfig();

        config.setMasterUrl(masterUrl);
        config.setMasterUsername(masterUsername);
        config.setMasterPassword(masterPassword);
        config.setMasterDatabaseName(masterDatabaseName);

        config.setNotificationUrl(notificationUrl);
        config.setNotificationUsername(notificationUsername);
        config.setNotificationPassword(notificationPassword);
        config.setNotificationDatabasePrefix(notificationDatabasePrefix);
        config.setNotificationDatabaseSuffix(notificationDatabaseSuffix);

        config.setRequestUrl(requestUrl);
        config.setRequestUsername(requestUsername);
        config.setRequestPassword(requestPassword);
        config.setRequestDatabasePrefix(requestDatabasePrefix);
        config.setRequestDatabaseSuffix(requestDatabaseSuffix);

        return config;
    }

    @Bean(name = "org.tiogasolutions.notify.kernel.config.TrustedUserStore")
    public TrustedUserStore trustedUserStore() {
        return new TrustedUserStore("admin:password");
    }


    @Bean(name = "org.tiogasolutions.notify.kernel.config.CouchEnvironment")
    public CouchEnvironment couchEnvironment() {
        return new CouchEnvironment().setTesting(false);
    }

    @Bean
    public NotifyApplication notifyApplication() {
        return new NotifyApplication();
    }

    @Bean
    public GrizzlyServer grizzlyServer(GrizzlyServerConfig grizzlyServerConfig, NotifyApplication application, ApplicationContext applicationContext) {

        ResourceConfig resourceConfig = ResourceConfig.forApplication(application);
        resourceConfig.property("contextConfig", applicationContext);
        resourceConfig.packages("org.tiogasolutions.notify");
        resourceConfig.register(RequestContextFilter.class, 1);

        return new GrizzlyServer(grizzlyServerConfig, resourceConfig);
    }

    @Bean
    public Notifier notifier(@Value("${notifier_couch_url}") String couchUrl,
                             @Value("${notifier_couch_database_name}") String databaseName,
                             @Value("${notifier_couch_username}") String username,
                             @Value("${notifier_couch_password}") String password,
                             @Value("${notifier_force_logger}") boolean forceLogger) {

        NotificationSender sender = forceLogger ?
                new LoggingNotificationSender() :
                new CouchNotificationSender(couchUrl, databaseName, username, password);

        return new Notifier(sender).onBegin(builder -> {
            builder.topic("Notify Engine");

            try {
                String hostname = java.net.InetAddress.getLocalHost().getHostName();
                builder.trait("source", System.getProperty("user.name") + "@" + hostname);

            } catch (UnknownHostException ignored) {
                builder.trait("source", System.getProperty("user.name") + "@" + "UNKNOWN");
            }
        });
    }
}
