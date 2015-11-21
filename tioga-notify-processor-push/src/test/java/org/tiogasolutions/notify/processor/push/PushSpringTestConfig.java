package org.tiogasolutions.notify.processor.push;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.push.client.MockPushServerClient;
import org.tiogasolutions.push.client.PushServerClient;

import static java.util.Collections.singletonList;

@Profile("test")
@Configuration
public class PushSpringTestConfig {

  @Bean
  PushConfig pushConfig() {
    return new PushConfig();
  }

  @Bean
  MockPushServerClient pushServerClient() {
    return new MockPushServerClient();
  }

  /** @noinspection SpringJavaAutowiringInspection*/
  @Bean
  public TaskProcessorExecutor taskProcessorExecutor(DomainKernel domainKernel, EventBus eventBus, PushConfig pushConfig, PushServerClient pushServerClient) {
    return new TaskProcessorExecutor(domainKernel, eventBus, singletonList(
      // new SwingTaskProcessor(),
      // new LoggerTaskProcessor(),
      new PushTaskProcessor(pushConfig, pushServerClient)
      // new SlackTaskProcessor(),
      // new SmtpTaskProcessor()
    ));
  }
}
