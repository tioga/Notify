package org.tiogasolutions.notify.engine.v1;

import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.net.InetMediaType;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;
import org.tiogasolutions.notify.pub.domain.DomainSummary;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

public class AdminResourceV1 {

  private final DomainKernel domainKernel;
  private final ExecutionManager executionManager;
  private final NotificationKernel notificationKernel;
  private final ReceiverExecutor receiverExecutor;
  private final TaskProcessorExecutor processorExecutor;
  private final EventBus eventBus;

  public AdminResourceV1(ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel, ReceiverExecutor receiverExecutor, TaskProcessorExecutor processorExecutor, EventBus eventBus) {
    this.eventBus = eventBus;
    this.domainKernel = domainKernel;
    this.executionManager = executionManager;
    this.notificationKernel = notificationKernel;
    this.receiverExecutor = receiverExecutor;
    this.processorExecutor = processorExecutor;
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getDefaultPage() {
    return Response.ok("{\"status\":\"200\"}", InetMediaType.APPLICATION_JSON_VALUE).build();
  }

  @GET
  @Path("/domains")
  @Produces({MediaType.APPLICATION_JSON})
  public QueryResult<DomainProfile> getDomainProfiles() {
    List<DomainProfile> domainProfiles = domainKernel.listActiveDomainProfiles();
    return ListQueryResult.newComplete(DomainProfile.class, domainProfiles);
  }

  @GET
  @Path("/domains/{domainName}")
  @Produces({MediaType.APPLICATION_JSON})
  public Response getDomainProfile(@PathParam("domainName") String domainName) {
    try {
      DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
      return Response.ok(domainProfile).build();

    } catch(ApiNotFoundException e) {
      return Response.status(404).entity(e).build();
    }
  }

  @PUT
  @Path("/domains/{domainName}")
  @Produces({MediaType.APPLICATION_JSON})
  public DomainProfile createDomain(@PathParam("domainName") String domainName) {
    return domainKernel.getOrCreateDomain(domainName);
  }

  @GET
  @Path("/domains/{domainName}/summary")
  @Produces({MediaType.APPLICATION_JSON})
  public Response getDomainSummary(@PathParam("domainName") String domainName) {
    try {
      DomainSummary summary = domainKernel.fetchSummary(domainName);
      return Response.ok(summary).build();

    } catch(ApiNotFoundException e) {
      return Response.status(404).entity(e).build();
    }
  }

  @Path("/domains/{domainName}/notifications")
  public NotificationsResourceV1 getNotificationsResourceV1(@Context Request request, @PathParam("domainName") String domainName) {
    DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
    // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
    executionManager.newApiContext(domainProfile);
    return new NotificationsResourceV1(request, executionManager, notificationKernel);
  }

  @Path("/domains/{domainName}/route-catalog")
  public RouteCatalogResourceV1 getRouteCatalogResourceV1(@PathParam("domainName") String domainName) {
    DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
    // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
    executionManager.newApiContext(domainProfile);
    return new RouteCatalogResourceV1(executionManager, domainKernel);
  }

  @Path("/domains/{domainName}/requests")
  public NotificationRequestResourceV1 getRequestResourceV1(@PathParam("domainName") String domainName) {
    DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
    // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
    executionManager.newApiContext(domainProfile);
    return new NotificationRequestResourceV1(executionManager, domainKernel, eventBus);
  }

  @Path("/domains/{domainName}/tasks")
  public TasksResourceV1 getTasksResourceV1(@PathParam("domainName") String domainName) {
    DomainProfile domainProfile = domainKernel.findByDomainName(domainName);
    // CRITICAL - I don't think this is safe, execution domain will continue to remain after call
    executionManager.newApiContext(domainProfile);
    return new TasksResourceV1(executionManager, domainKernel, notificationKernel);
  }

  @Path("/system")
  public SystemResourceV1 getSystemResourceV1() {
    return new SystemResourceV1(receiverExecutor, processorExecutor);
  }
}
