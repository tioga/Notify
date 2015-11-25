package org.tiogasolutions.notify.server.grizzly;

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

import java.util.ArrayList;
import java.util.Arrays;

@Profile("hosted")
@Configuration
public class GrizzlySpringHostedConfig {

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
}
