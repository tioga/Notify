package org.tiogasolutions.notify.kernel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;

import static java.util.Collections.emptyList;

@Profile("test")
@Configuration
public class KernelSpringTestConfig {

    /**
     * @noinspection SpringJavaAutowiringInspection
     */
    @Bean
    public TaskProcessorExecutor taskProcessorExecutor(DomainKernel domainKernel, EventBus eventBus) {
        return new TaskProcessorExecutor(domainKernel, eventBus, emptyList());
    }

    @Bean
    Notifier notifier() {
        return new Notifier(new LoggingNotificationSender());
    }
}
