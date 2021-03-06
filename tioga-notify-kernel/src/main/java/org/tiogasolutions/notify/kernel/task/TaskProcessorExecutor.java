package org.tiogasolutions.notify.kernel.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.exceptions.ApiConflictException;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.event.TaskEventListener;
import org.tiogasolutions.notify.kernel.notification.NotificationDomain;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.task.TaskQuery;
import org.tiogasolutions.notify.pub.task.TaskResponse;
import org.tiogasolutions.notify.pub.task.TaskStatus;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class TaskProcessorExecutor implements TaskEventListener {

    private static final String NAME = TaskProcessorExecutor.class.getSimpleName();
    private static final Logger log = LoggerFactory.getLogger(TaskProcessorExecutor.class);
    private final DomainKernel domainKernel;
    private final Map<TaskProcessorType, TaskProcessor> processorMap = new HashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final ExecutorService threadPoolExecutor;
    private final Notifier notifier;
    private TaskProcessorExecutorStatus executorStatus;
    private ScheduledFuture executorFuture = null;

    public TaskProcessorExecutor(DomainKernel domainKernel, EventBus eventBus, Notifier notifier, List<TaskProcessor> taskProcessors) {
        this.notifier = notifier;
        this.domainKernel = domainKernel;
        this.threadPoolExecutor = Executors.newCachedThreadPool();
        this.executorStatus = TaskProcessorExecutorStatus.STOPPED;

        for (TaskProcessor processor : taskProcessors) {
            TaskProcessorType type = processor.getType();

            if (processorMap.containsKey(type)) {
                String msg = format("The processor type \"%s\" has already been registered.", type);
                throw new IllegalArgumentException(msg);
            }

            processorMap.put(type, processor);
        }

        eventBus.subscribe(this);
    }

    public TaskProcessorExecutorStatus getExecutorStatus() {
        return executorStatus;
    }

    public synchronized void start() {
        if (executorFuture != null) {
            throw new IllegalStateException(NAME + " is already started.");
        }

        // TODO - this delay values should come from the config.
        // Schedule the executor
        executorFuture = scheduledExecutorService.scheduleWithFixedDelay(this::execute, 15, 60, TimeUnit.SECONDS);

        // Change our status.
        this.executorStatus = TaskProcessorExecutorStatus.IDLE;

        log.info(NAME + " started, now idle.");
    }

    public synchronized void stop() {
        // Stop the executor
        if (executorFuture != null) {
            executorFuture.cancel(false);
            executorFuture = null;
        }
        executorStatus = TaskProcessorExecutorStatus.STOPPED;

        log.info(NAME + " stopped.");
    }

    @PreDestroy
    private void shutdown() {
        stop();
        scheduledExecutorService.shutdown();
    }

    @Override
    public void taskCreated(String domainName, TaskEntity task, Notification notification) {
        try {
            NotificationDomain notificationDomain = domainKernel.notificationDomain(domainName);
            Map<String, List<TaskEntity>> tasks = new HashMap<>();
            tasks.put(task.getDestination().getProvider(), Arrays.asList(task));
            processTasksByProvider(notificationDomain, tasks);

        } catch (Exception e) {
            notify(notification, e, format("Unexpected exception while processing task (domain=%s, notification=%s, task=%s).",
                    domainName, notification.getNotificationId(), task.getTaskId()));
        }
    }

    /**
     * Allows for direct execution of the processor
     */
    public void execute() {
        if (running.compareAndSet(false, true)) {
            log.debug(NAME + "is executing.");
            executorStatus = TaskProcessorExecutorStatus.EXECUTING;

            try {
                List<NotificationDomain> activeNotificationDomains = domainKernel.listActiveNotificationDomains();
                activeNotificationDomains.forEach(this::processDomain);

            } catch (Exception e) {
                notify(null, e, "Unexpected exception executing processor.");

            } finally {
                executorStatus = TaskProcessorExecutorStatus.IDLE;
                log.debug(NAME + " finished, now idle.");
                running.set(false);
            }

        } else {
            log.debug(NAME + " already running.");
        }
    }

    private void processDomain(NotificationDomain notificationDomain) {

        String domainName = notificationDomain.getDomainName();
        log.trace("Processing all tasks for domain {}.", domainName);

        try {
            // Find all pending tasks for this domain.
            QueryResult<TaskEntity> pendingTasks = notificationDomain.query(new TaskQuery().setTaskStatus(TaskStatus.PENDING));
            if (pendingTasks.isNotEmpty()) {
                // Map our tasks by provider.
                Map<String, List<TaskEntity>> tasksMappedByProvider = mapTasksByProvider(pendingTasks);
                processTasksByProvider(notificationDomain, tasksMappedByProvider);
            }
        } catch (Exception e) {
            throw ApiException.internalServerError(format("Exception processing the domain %s", domainName));
        }
    }

    private void processTasksByProvider(NotificationDomain notificationDomain, Map<String, List<TaskEntity>> tasksMappedByProvider) {
        for (Map.Entry<String, List<TaskEntity>> entry : tasksMappedByProvider.entrySet()) {
            TaskProcessor processor = findTaskProcessor(entry.getKey());
            if (processor == null) {
                List<String> ids = entry.getValue().stream().map(TaskEntity::getNotificationId).collect(Collectors.toList());
                String msg = String.format("A processor was not found for %s, skipping %s tasks: %s", entry.getKey(), entry.getValue().size(), ids);
                notify(null, new NullPointerException(), msg);
                return;
            }
            processTaskForProvider(notificationDomain, processor, entry.getValue());
        }
    }

    private TaskProcessor findTaskProcessor(String providerName) {
        TaskProcessorType processorType = TaskProcessorType.valueOf(providerName);
        return processorMap.get(processorType);
    }

    private Map<String, List<TaskEntity>> mapTasksByProvider(QueryResult<TaskEntity> result) {

        Map<String, List<TaskEntity>> map = new HashMap<>();

        for (TaskEntity task : result) {
            try {
                Destination destination = task.getDestination();
                String provider = destination.getProvider();
                if (map.containsKey(provider) == false) {
                    map.put(provider, new ArrayList<>());
                }
                map.get(provider).add(task);

            } catch (Exception e) {
                notify(null, e, String.format("Weird bug mapping tasks to a provider (notification=%s, task=%s).", task.getNotificationId(), task.getTaskId()));
            }
        }

        return map;
    }

    private void processTaskForProvider(NotificationDomain notificationDomain, TaskProcessor processor, List<TaskEntity> tasks) {
        log.debug("Processing {} provider's tasks for domain {}.", processor.getType(), notificationDomain.getDomainName());

        if (processor.isReady() == false) {
            // This particular processor is not ready. We will have to
            // skip these and hope that it becomes ready later.
            log.warn("The {} provider is not ready to process tasks for the domain {}.", processor.getType(), notificationDomain.getDomainName());
            return;
        }

        for (TaskEntity task : tasks) {
            Notification notification = notificationDomain.findNotificationById(task.getNotificationId()).toNotification();
            processTask(notificationDomain, processor, task, notification);
        }
    }

    private void processTask(NotificationDomain notificationDomain,
                             TaskProcessor processor,
                             final TaskEntity taskEntity,
                             Notification notification) {

        Callable<Void> taskProcessorCallable = () -> {

            // Set task to "sending".
            String domainName = notificationDomain.getDomainName();
            TaskEntity localTaskEntity = taskEntity;

            try {
                localTaskEntity.sending();
                localTaskEntity = notificationDomain.saveAndReload(localTaskEntity);

            } catch (ApiNotFoundException | ApiConflictException ex) {
                // We will change the state back to pending because the update presumably failed.
                localTaskEntity.pending();

                String msg = (ex instanceof ApiNotFoundException) ?
                        format("Cannot find task for domain %s: %s", domainName, localTaskEntity.getLabel()) :
                        format("DB conflict processing task for domain %s, (already processed?): %s", domainName, localTaskEntity.getLabel());

                notify(notification, ex, msg);

                // Return so we don't attempt to process
                return null;

            } catch (Exception ex) {
                notify(notification, ex, "Exception setting task to sending");

                // Return so we don't attempt to process
                return null;
            }

            // Process the task.
            String processorName = "n/a";
            try {
                processorName = processor.getType().getCode();
                DomainProfile domainProfile = domainKernel.getOrCreateDomain(domainName);

                log.info("Begin processing task for domain {} with processor {}: {}",
                        domainName, processorName, localTaskEntity.getLabel());

                // Send to processor
                TaskResponse taskResponse = processor.processTask(domainProfile, notification, localTaskEntity.toTask());
                log.info("The task {} for notification {} {}", localTaskEntity.getTaskId(), localTaskEntity.getNotificationId(), taskResponse.getResponseAction().pastTense);

                // Assign response and save.
                localTaskEntity.response(taskResponse);
                notificationDomain.save(localTaskEntity);

                log.debug("Finished processing task for domain {} and processor {} with response action {}: {}",
                        domainName, processorName, taskResponse.getResponseAction(), localTaskEntity.getLabel());

            } catch (Exception e) {
                // Processors should not throw exceptions, but if it does fail the task.
                TaskResponse taskResponse = TaskResponse.fail("Exception thrown from task processor", e);
                localTaskEntity.response(taskResponse);
                notificationDomain.save(localTaskEntity);

                notify(notification, e, format("Exception processing task (domain=%s, processor=%s, notification=%s, task=%s)",
                        domainName, processorName, notification.getNotificationId(), localTaskEntity.getTaskId()));
            }
            return null;
        };

        // We would like to think that the processor is not going to block
        // our thread but we have no guarantee of that so we take ownership
        // of the thread pool here. It also means that the actual processor
        // can be that much simpler in it's construction.
        threadPoolExecutor.submit(taskProcessorCallable);

    }

    private void notify(Notification notification, Exception e, String msg) {
        try {
            if (notification != null && notification.isInternalException()) log.error("SUPPRESSED: "+msg, e);
            else notifier.begin().summary(msg).exception(e).send().get();
        } catch (Exception ex) {
            log.error("Exception sending notification", ex);
        }
    }
}
