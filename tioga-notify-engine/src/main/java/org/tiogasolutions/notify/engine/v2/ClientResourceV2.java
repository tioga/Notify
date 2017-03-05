package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

public class ClientResourceV2 {

  private final EventBus eventBus;
  private final DomainKernel domainKernel;
  private final ExecutionManager executionManager;
  private final NotificationKernel notificationKernel;

  public ClientResourceV2(ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel, EventBus eventBus) {
    this.eventBus = eventBus;
    this.domainKernel = domainKernel;
    this.executionManager = executionManager;
    this.notificationKernel = notificationKernel;
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public DomainProfile getDomainProfile() {
    String domainName = executionManager.context().getDomainName();
    return domainKernel.findByDomainName(domainName);
  }

  @GET
  @Path("summary")
  @Produces({MediaType.APPLICATION_JSON})
  public Response getDomainSummary() {
    try {
      String domainName = executionManager.context().getDomainName();
      DomainSummary summary = domainKernel.fetchSummary(domainName);
      return Response.ok(summary).build();

    } catch(ApiNotFoundException e) {
      return Response.status(404).entity(e).build();
    }
  }

  @Path("/notifications")
  public NotificationsResourceV2 getNotificationsResource(@Context Request request) {
    return new NotificationsResourceV2(request, executionManager, notificationKernel);
  }

  @Path("/route-catalog")
  public RouteCatalogResourceV2 getRouteCatalogResource() {
    return new RouteCatalogResourceV2(executionManager, domainKernel);
  }

  @Path("/requests")
  public NotificationRequestResourceV2 getRequestResourceV1() {
    return new NotificationRequestResourceV2(executionManager, domainKernel, eventBus);
  }

  @Path("/simple-request-entry")
  public SimpleRequestEntryResourceV2 getSimpleRequestEntryV1() {
    return new SimpleRequestEntryResourceV2(executionManager, domainKernel, eventBus);
  }
}
