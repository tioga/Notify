package org.tiogasolutions.notify.server.grizzly;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.NotifyObjectMapper;
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
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

import java.util.Arrays;

@Profile("hosted")
@Configuration
public class NotifyHostedSpringConfig {

  private String getContext() {
    return EnvUtils.findProperty("notify.context", "");
  }

  private int getPort() {
    String value = EnvUtils.findProperty("notify.port", "8080");
    return Integer.valueOf(value);
  }

  private int getShutdownPort() {
    String value = EnvUtils.findProperty("notify.shutdownPort", "8081");
    return Integer.valueOf(value);
  }

  private String getHostName() {
    return EnvUtils.findProperty("notify.hostName", "localhost");
  }

  private String getMasterUrl() {
    return EnvUtils.requireProperty("notify.masterUrl");
  }

  private String getMasterUsername() {
    return EnvUtils.requireProperty("notify.masterUsername");
  }

  private String getMasterPassword() {
    return EnvUtils.requireProperty("notify.masterPassword");
  }

  private String getMasterDatabaseName() {
    return EnvUtils.requireProperty("notify.masterDatabaseName");
  }

  private String getNotificationUrl() {
    return EnvUtils.requireProperty("notify.notificationUrl");
  }

  private String getNotificationUsername() {
    return EnvUtils.requireProperty("notify.notificationUsername");
  }

  private String getNotificationPassword() {
    return EnvUtils.requireProperty("notify.notificationPassword");
  }

  private String getNotificationDatabasePrefix() {
    return EnvUtils.requireProperty("notify.notificationDatabasePrefix");
  }

  private String getNotificationDatabaseSuffix() {
    return EnvUtils.requireProperty("notify.notificationDatabaseSuffix");
  }

  private String getRequestUrl() {
    return EnvUtils.requireProperty("notify.requestUrl");
  }

  private String getRequestUsername() {
    return EnvUtils.requireProperty("notify.requestUsername");
  }

  private String getRequestPassword() {
    return EnvUtils.requireProperty("notify.requestPassword");
  }

  private String getRequestDatabasePrefix() {
    return EnvUtils.requireProperty("notify.requestDatabasePrefix");
  }

  private String getRequestDatabaseSuffix() {
    return EnvUtils.requireProperty("notify.requestDatabaseSuffix");
  }

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
    config.setHostName(getHostName());
    config.setPort(getPort());
    config.setShutdownPort(getShutdownPort());
    config.setContext(getContext());
    config.setToOpenBrowser(false);
    return config;
  }

  @Bean(name = "org.tiogasolutions.notify.kernel.config.CouchServersConfig")
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl(getMasterUrl());
    config.setMasterUsername(getMasterUsername());
    config.setMasterPassword(getMasterPassword());
    config.setMasterDatabaseName(getMasterDatabaseName());

    config.setNotificationUrl(getNotificationUrl());
    config.setNotificationUsername(getNotificationUsername());
    config.setNotificationPassword(getNotificationPassword());
    config.setNotificationDatabasePrefix(getNotificationDatabasePrefix());
    config.setNotificationDatabaseSuffix(getNotificationDatabaseSuffix());

    config.setRequestUrl(getRequestUrl());
    config.setRequestUsername(getRequestUsername());
    config.setRequestPassword(getRequestPassword());
    config.setRequestDatabasePrefix(getRequestDatabasePrefix());
    config.setRequestDatabaseSuffix(getRequestDatabaseSuffix());

    return config;
  }

  @Bean(name="org.tiogasolutions.notify.kernel.config.TrustedUserStore")
  public TrustedUserStore trustedUserStore() {
    return new TrustedUserStore("admin:password");
  }


  @Bean(name="org.tiogasolutions.notify.kernel.config.CouchEnvironment")
  public CouchEnvironment couchEnvironment() {
    return new CouchEnvironment().setTesting(true);
  }
}
