package org.tiogasolutions.notify.server.grizzly;

import org.glassfish.jersey.server.ResourceConfig;
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
import org.tiogasolutions.notify.processor.logger.LoggerTaskProcessor;
import org.tiogasolutions.notify.processor.push.LivePushClientFactory;
import org.tiogasolutions.notify.processor.push.PushTaskProcessor;
import org.tiogasolutions.notify.processor.slack.SlackTaskProcessor;
import org.tiogasolutions.notify.processor.smtp.SmtpTaskProcessor;
import org.tiogasolutions.notify.processor.swing.SwingTaskProcessor;
import org.tiogasolutions.runners.grizzly.GrizzlyServer;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.util.Arrays;

import static org.tiogasolutions.dev.common.EnvUtils.findProperty;
import static org.tiogasolutions.dev.common.EnvUtils.requireProperty;

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

  @Bean(name="org.tiogasolutions.notify.kernel.config.SystemConfiguration")
  public SystemConfiguration systemConfiguration() {
    return new SystemConfiguration("*", "/api/v1/client", "/api/v1/admin");
  }

  @Bean(name="org.tiogasolutions.notify.engine.web.readers.ExternalizedStaticContentReader")
  BundledStaticContentReader bundledStaticContentReader() {
    return new BundledStaticContentReader("/org/tiogasolutions/notify/admin/app");
  }

  @Bean(name="org.tiogasolutions.runners.grizzly.GrizzlyServerConfig")
  public GrizzlyServerConfig grizzlyServerConfig() {
    GrizzlyServerConfig config = new GrizzlyServerConfig();
    config.setHostName(findProperty("notify.hostName", "0.0.0.0"));
    config.setPort(Integer.valueOf(findProperty("notify.port", "8080")));
    config.setShutdownPort(Integer.valueOf(findProperty("notify.shutdownPort", "8081")));
    config.setContext(findProperty("notify.context", ""));
    config.setToOpenBrowser(false);
    return config;
  }

  @Bean(name = "org.tiogasolutions.notify.kernel.config.CouchServersConfig")
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl(requireProperty("notify.masterUrl"));
    config.setMasterUsername(requireProperty("notify.masterUsername"));
    config.setMasterPassword(requireProperty("notify.masterPassword"));
    config.setMasterDatabaseName(requireProperty("notify.masterDatabaseName"));

    config.setNotificationUrl(requireProperty("notify.notificationUrl"));
    config.setNotificationUsername(requireProperty("notify.notificationUsername"));
    config.setNotificationPassword(requireProperty("notify.notificationPassword"));
    config.setNotificationDatabasePrefix(requireProperty("notify.notificationDatabasePrefix"));
    config.setNotificationDatabaseSuffix(requireProperty("notify.notificationDatabaseSuffix"));

    config.setRequestUrl(requireProperty("notify.requestUrl"));
    config.setRequestUsername(requireProperty("notify.requestUsername"));
    config.setRequestPassword(requireProperty("notify.requestPassword"));
    config.setRequestDatabasePrefix(requireProperty("notify.requestDatabasePrefix"));
    config.setRequestDatabaseSuffix(requireProperty("notify.requestDatabaseSuffix"));

    return config;
  }

  @Bean(name="org.tiogasolutions.notify.kernel.config.TrustedUserStore")
  public TrustedUserStore trustedUserStore() {
    return new TrustedUserStore("admin:password");
  }


  @Bean(name="org.tiogasolutions.notify.kernel.config.CouchEnvironment")
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

    return new GrizzlyServer(grizzlyServerConfig, resourceConfig);
  }
}
