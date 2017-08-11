package org.tiogasolutions.notify.engine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.notify.NotifyObjectMapper;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.notify.engine.web.readers.MockContentReader;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.LoggingNotificationSender;

import static java.util.Collections.emptyList;

@Profile("test")
@Configuration
public class EngineSpringTestConfig {

    @Bean
    public NotifyObjectMapper notifyObjectMapper() {
        return new NotifyObjectMapper();
    }

    @Bean
    public MockContentReader mockContentReader() {
        return new MockContentReader();
    }

    @Bean
    NotifyApplication notifyApplication() {
        return new NotifyApplication();
    }

    /**
     * @noinspection SpringJavaAutowiringInspection
     */
    @Bean
    public TaskProcessorExecutor taskProcessorExecutor(DomainKernel domainKernel, EventBus eventBus, Notifier notifier) {
        return new TaskProcessorExecutor(domainKernel, eventBus, notifier, emptyList());
        // new SwingTaskProcessor(),
        // new LoggerTaskProcessor(),
        // new PushTaskProcessor(pushConfig, pushServerClient)
        // new SlackTaskProcessor(),
        // new SmtpTaskProcessor()
    }

    @Bean
    Notifier notifier() {
        return new Notifier(new LoggingNotificationSender());
    }
}
