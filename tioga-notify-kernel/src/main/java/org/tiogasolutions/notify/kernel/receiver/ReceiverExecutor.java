package org.tiogasolutions.notify.kernel.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ReceiverExecutor implements BeanFactoryAware {

    private static final String NAME = ReceiverExecutor.class.getSimpleName();
    private static final Logger log = LoggerFactory.getLogger(ReceiverExecutor.class);
    private final DomainKernel domainKernel;
    private final List<RequestReceiver> _receivers = new ArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final NotificationKernel notificationKernel;
    private ReceiverExecutorStatus executorStatus;
    private ScheduledFuture executorFuture = null;
    private BeanFactory beanFactory;

    @Autowired
    public ReceiverExecutor(DomainKernel domainKernel,
                            NotificationKernel notificationKernel) {

        this.executorStatus = ReceiverExecutorStatus.STOPPED;
        this.domainKernel = domainKernel;
        this.notificationKernel = notificationKernel;
    }

    public ReceiverExecutorStatus getExecutorStatus() {
        return executorStatus;
    }

    public synchronized void start() {
        if (executorFuture != null) {
            throw new IllegalStateException(NAME + " is already started.");
        }

        // TODO - this delay values should come from the config.
        // Schedule the executor
        executorFuture = scheduledExecutorService.scheduleWithFixedDelay(this::execute, 5, 5, TimeUnit.SECONDS);

        // Change our status.
        this.executorStatus = ReceiverExecutorStatus.IDLE;

        log.info(NAME + " started, now idle.");
    }

    public synchronized void stop() {
        // Stop the executor
        if (executorFuture != null) {
            executorFuture.cancel(false);
            executorFuture = null;
        }
        executorStatus = ReceiverExecutorStatus.STOPPED;

        log.info(NAME + " stopped.");
    }

    @PreDestroy
    private void shutdown() {
        stop();
        scheduledExecutorService.shutdown();
    }

    /**
     * Allows for direct execution of the receiver
     */
    public void execute() {
        if (running.compareAndSet(false, true)) {
            log.trace("ReceiverExecutor is executing.");
            executorStatus = ReceiverExecutorStatus.EXECUTING;

            try {
                executeAllDomains();

            } catch (Exception e) {
                // TODO plugin a notifier or something here
                log.error("Unexpected exception during processing.", e);

            } finally {
                executorStatus = ReceiverExecutorStatus.IDLE;
                log.trace("ReceiverExecutor finished, now idle.");
                running.set(false);
            }

        } else {
            log.debug(NAME + " already running.");
        }
    }

    protected void executeAllDomains() {
        List<DomainProfile> activeProfiles = domainKernel.listActiveDomainProfiles();
        activeProfiles.stream().forEach(this::executeAllReceivers);
    }

    public List<RequestReceiver> getReceivers() {
        if (_receivers.isEmpty()) {
            // Later this can become dynamic.
            ExecutionManager executionManager = beanFactory.getBean(ExecutionManager.class);
            _receivers.add(new CouchRequestReceiver(domainKernel, notificationKernel, executionManager));
        }
        return _receivers;
    }

    protected void executeAllReceivers(DomainProfile domainProfile) {
        for (RequestReceiver receiver : getReceivers()) {
            // Receiver should handle it's own exceptions but just in case.
            String receiverName = receiver.getClass().getName();
            String domainName = domainProfile.getDomainName();

            try {
                log.trace("Executing receiver {} for domain {}", receiverName, domainName);
                receiver.receiveRequests(domainProfile);

            } catch (Throwable t) {
                String msg = String.format("Unexpected exception processing receiver %s for domain %s.", receiverName, domainName);
                log.error(msg, t);
            }
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
