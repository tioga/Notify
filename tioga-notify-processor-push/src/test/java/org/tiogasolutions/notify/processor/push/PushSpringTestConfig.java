package org.tiogasolutions.notify.processor.push;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;

import static java.util.Collections.singletonList;

@Profile("test")
@Configuration
public class PushSpringTestConfig {

    @Bean
    public PushClientFactory pushClientFactory() {
        return new MockPushClientFactory();
    }

    /**
     * @noinspection SpringJavaAutowiringInspection
     */
    @Bean
    public TaskProcessorExecutor taskProcessorExecutor(DomainKernel domainKernel, EventBus eventBus, PushClientFactory pushClientFactory) {
        return new TaskProcessorExecutor(domainKernel, eventBus, singletonList(
                new PushTaskProcessor(pushClientFactory)
        ));
    }

    @Bean
    Notifier notifier() {
        return new Notifier(new LoggingNotificationSender());
    }
}
