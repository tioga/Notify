package org.tiogasolutions.notify.kernel.execution;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

@Component
public class ExecutionManager implements ExecutionAccessor {

    private final EventBus eventBus;
    private final DomainKernel domainKernel;
    private final NotificationKernel notificationKernel;
    private final JsonTranslator translator;
    private final ThreadLocal<ExecutionContext> threadLocal = new ThreadLocal<>();
    private final ReceiverExecutor receiverExecutor;
    private final TaskProcessorExecutor processorExecutor;

    @Autowired
    public ExecutionManager(EventBus eventBus, DomainKernel domainKernel, NotificationKernel notificationKernel, ReceiverExecutor receiverExecutor, TaskProcessorExecutor processorExecutor, ObjectMapper objectMapper) {
        this.eventBus = eventBus;
        this.domainKernel = domainKernel;
        this.receiverExecutor = receiverExecutor;
        this.processorExecutor = processorExecutor;
        this.notificationKernel = notificationKernel;
        this.translator = new TiogaJacksonTranslator(objectMapper);
    }

    public void clearContext() {
        threadLocal.remove();
    }

    public ReceiverExecutor getReceiverExecutor() {
        return receiverExecutor;
    }

    public TaskProcessorExecutor getProcessorExecutor() {
        return processorExecutor;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public JsonTranslator getTranslator() {
        return translator;
    }

    public DomainKernel getDomainKernel() {
        return domainKernel;
    }

    public NotificationKernel getNotificationKernel() {
        return notificationKernel;
    }

    public ExecutionContext newSystemContext() {
        ExecutionContext context = new ExecutionContext(domainKernel.getSystemDomain());
        assignContext(context);
        return context;
    }

    public ExecutionContext newSystemContext(UriInfo uriInfo, HttpHeaders httpHeaders) {
        ExecutionContext context = new ExecutionContext(domainKernel.getSystemDomain(), uriInfo, httpHeaders);
        assignContext(context);
        return context;
    }

    public ExecutionContext newApiContext(String apiKey, UriInfo uriInfo, HttpHeaders httpHeaders) {
        DomainProfile domainProfile = domainKernel.findByApiKey(apiKey);
        ExecutionContext context = new ExecutionContext(domainProfile, uriInfo, httpHeaders);
        assignContext(context);
        return context;
    }

    public ExecutionContext newApiContext(DomainProfile domainProfile, UriInfo uriInfo, HttpHeaders httpHeaders) {
        ExecutionContext context = new ExecutionContext(domainProfile, uriInfo, httpHeaders);
        assignContext(context);
        return context;
    }

    public ExecutionContext newApiContext(String apiKey) {
        DomainProfile domainProfile = domainKernel.findByApiKey(apiKey);
        ExecutionContext context = new ExecutionContext(domainProfile);
        assignContext(context);
        return context;
    }

    public ExecutionContext newApiContext(DomainProfile domainProfile) {
        ExecutionContext context = new ExecutionContext(domainProfile);
        assignContext(context);
        return context;
    }

    public void assignContext(ExecutionContext context) {
        threadLocal.set(context);
    }

    @Override
    public boolean hasContext() {
        return threadLocal.get() != null;
    }

    @Override
    // TODO - why is this not getContext()?
    public ExecutionContext context() {
        ExecutionContext context = threadLocal.get();
        if (context == null) {
            throw ApiException.internalServerError("There is no current execution context for this thread.");
        } else {
            return context;
        }
    }

    @Override
    public String domainName() {
        return context().getDomainName();
    }
}
