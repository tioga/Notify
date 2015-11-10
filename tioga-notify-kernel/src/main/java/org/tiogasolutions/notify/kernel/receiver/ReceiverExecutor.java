package org.tiogasolutions.notify.kernel.receiver;

import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: Harlan
 * Date: 2/12/2015
 * Time: 9:08 PM
 */
@Named
public class ReceiverExecutor {

  private static final String NAME = ReceiverExecutor.class.getSimpleName();
  private static final Logger log = LoggerFactory.getLogger(ReceiverExecutor.class);

  private ReceiverExecutorStatus executorStatus;
  private final DomainKernel domainKernel;
  private final List<RequestReceiver> receivers = new ArrayList<>();

  private final AtomicBoolean running = new AtomicBoolean(false);
  private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
  private ScheduledFuture executorFuture = null;

  @Inject
  public ReceiverExecutor(DomainKernel domainKernel,
                          NotificationKernel notificationKernel,
                          ExecutionManager executionManager) {

    this.executorStatus = ReceiverExecutorStatus.STOPPED;
    this.domainKernel = domainKernel;

    // Later this can become dynamic.
    receivers.add(new CouchRequestReceiver(domainKernel, notificationKernel, executionManager));
  }

  public ReceiverExecutorStatus getExecutorStatus() {
    return executorStatus;
  }

  public synchronized void start() {
    if (executorFuture != null) {
      throw new IllegalStateException(NAME+" is already started.");
    }

    // TODO - this delay values should come from the config.
    // Schedule the executor
    executorFuture = scheduledExecutorService.scheduleWithFixedDelay(this::execute, 30, 30, TimeUnit.SECONDS);

    // Change our status.
    this.executorStatus = ReceiverExecutorStatus.IDLE;

    log.info(NAME+" started, now idle.");
  }

  public synchronized void stop() {
    // Stop the executor
    if (executorFuture != null) {
      executorFuture.cancel(false);
      executorFuture = null;
    }
    executorStatus = ReceiverExecutorStatus.STOPPED;

    log.info(NAME+" stopped.");
  }

  @PreDestroy
  private void shutdown() {
    stop();
    scheduledExecutorService.shutdown();
  }

  /** Allows for direct execution of the receiver */
  public void execute() {
    if (running.compareAndSet(false, true)) {
      log.debug(NAME+" is executing.");
      executorStatus = ReceiverExecutorStatus.EXECUTING;

      try {
        executeAllDomains();

      } catch (Exception e) {
        // TODO plugin a notifier or something here
        log.error("Unexpected exception during processing.", e);

      } finally {
        executorStatus = ReceiverExecutorStatus.IDLE;
        log.debug(NAME+" finished, now idle.");
        running.set(false);
      }

    } else {
      log.debug(NAME+" already running.");
    }
  }

  protected void executeAllDomains() {
    List<DomainProfile> activeProfiles = domainKernel.listActiveDomainProfiles();
    activeProfiles.stream().forEach(this::executeAllReceivers);
  }

  protected void executeAllReceivers(DomainProfile domainProfile) {
    for(RequestReceiver receiver : receivers) {
      // Receiver should handle it's own exceptions but just in case.
      String receiverName = receiver.getClass().getName();
      String domainName = domainProfile.getDomainName();

      try {
        log.debug("Executing receiver {} for domain {}", receiverName, domainName);
        receiver.receiveRequests(domainProfile);

      } catch (Throwable t) {
        String msg = String.format("Unexpected exception processing receiver %s for domain %s.", receiverName, domainName);
        log.error(msg, t);
      }
    }
  }
}
