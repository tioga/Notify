package org.tiogasolutions.notify.server.grizzly;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.common.BeanUtils;
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

import java.util.ArrayList;
import java.util.Arrays;

@Profile("hosted")
@Configuration
public class NotifyHostedSpringConfig {

  @Value("#{systemEnvironment.context}")
  private String context;

  @Value("#{systemEnvironment.port}")
  private int port = 8080;

  @Value("#{systemEnvironment.shutdownPort}")
  private int shutdownPort = 8081;

  @Value("#{systemEnvironment.hostName}")
  private String hostName = "0.0.0.0";

  @Value("#{systemEnvironment.masterUrl}")
  private String masterUrl;

  @Value("#{systemEnvironment.masterUsername}")
  private String masterUsername;

  @Value("#{systemEnvironment.masterPassword}")
  private String masterPassword;

  @Value("#{systemEnvironment.masterDatabaseName}")
  private String masterDatabaseName;

  @Value("#{systemEnvironment.domainUrl}")
  private String domainUrl;

  @Value("#{systemEnvironment.domainUsername}")
  private String domainUsername;

  @Value("#{systemEnvironment.domainPassword}")
  private String domainPassword;

  @Value("#{systemEnvironment.domainDatabasePrefix}")
  private String domainDatabasePrefix;

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

  @Bean
  public SystemConfiguration systemConfiguration() {
    return new SystemConfiguration("*", "/api/v1/client", "/api/v1/admin");
  }

  @Bean
  BundledStaticContentReader bundledStaticContentReader() {
    return new BundledStaticContentReader("/org/tiogasolutions/notify/admin/app");
  }

  @Bean
  public GrizzlyServerConfig grizzlyServerConfig() {
    GrizzlyServerConfig config = new GrizzlyServerConfig();
    config.setHostName(hostName);
    config.setPort(port);
    config.setShutdownPort(shutdownPort);
    config.setContext(context);
    config.setToOpenBrowser(false);
    return config;
  }

  @Bean
  public CouchServersConfig couchServersConfig() {
    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl(masterUrl);
    config.setMasterUsername(masterUsername);
    config.setMasterPassword(masterPassword);
    config.setMasterDatabaseName(masterDatabaseName);

    config.setNotificationUrl(domainUrl);
    config.setNotificationUserName(domainUsername);
    config.setNotificationPassword(domainPassword);
    config.setNotificationDatabasePrefix(domainDatabasePrefix);

    return config;
  }
}
