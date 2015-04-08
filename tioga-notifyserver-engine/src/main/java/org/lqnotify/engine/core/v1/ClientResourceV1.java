package org.lqnotify.engine.core.v1;

import org.lqnotify.kernel.EventBus;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.execution.ExecutionManager;
import org.lqnotify.kernel.notification.NotificationKernel;
import org.lqnotify.pub.DomainProfile;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class ClientResourceV1 {

  private final EventBus eventBus;
  private final DomainKernel domainKernel;
  private final ExecutionManager executionManager;
  private final NotificationKernel notificationKernel;

  public ClientResourceV1(ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernel, EventBus eventBus) {
    this.eventBus = eventBus;
    this.domainKernel = domainKernel;
    this.executionManager = executionManager;
    this.notificationKernel = notificationKernel;
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public DomainProfile getDefaultPage() {
    String domainName = executionManager.context().getDomainName();
    return domainKernel.findByDomainName(domainName);
  }

  @Path("/notifications")
  public NotificationsResourceV1 getNotificationsResource() {
    return new NotificationsResourceV1(executionManager, notificationKernel);
  }

  @Path("/route-catalog")
  public RouteCatalogResourceV1 getRouteCatalogResource() {
    return new RouteCatalogResourceV1(executionManager, domainKernel);
  }

  @Path("/requests")
  public RequestResourceV1 getRequestResourceV1() {
    return new RequestResourceV1(executionManager, domainKernel, eventBus);
  }

  @Path("/simple-request-entry")
  public SimpleRequestEntryResourceV1 getSimpleRequestEntryV1() {
    return new SimpleRequestEntryResourceV1(executionManager, domainKernel, eventBus);
  }
}
