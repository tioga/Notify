package org.lqnotify.engine.core;

import org.lqnotify.engine.core.v1.AdminResourceV1;
import org.lqnotify.engine.core.v1.ClientResourceV1;
import org.lqnotify.engine.web.SystemStatus;
import org.lqnotify.kernel.EventBus;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.execution.ExecutionManager;
import org.lqnotify.kernel.notification.NotificationKernel;
import org.lqnotify.kernel.receiver.ReceiverExecutor;
import org.lqnotify.kernel.processor.ProcessorExecutor;
import org.lqnotify.engine.web.StaticContentReader;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/")
public class EngRootResource {

  @Context
  UriInfo uriInfo;

  @Inject // Injected by CDI, not Spring
  @SuppressWarnings("SpringJavaAutowiringInspection")
  private DomainKernel domainKernel;

  @Inject // Injected by CDI, not Spring
  @SuppressWarnings("SpringJavaAutowiringInspection")
  private ExecutionManager executionManager;

  @Inject // Injected by CDI, not Spring
  @SuppressWarnings("SpringJavaAutowiringInspection")
  private NotificationKernel notificationKernel;

  @Inject // Injected by CDI, not Spring
  @SuppressWarnings("SpringJavaAutowiringInspection")
  private ReceiverExecutor receiverExecutor;

  @Inject // Injected by CDI, not Spring
  @SuppressWarnings("SpringJavaAutowiringInspection")
  private ProcessorExecutor processorExecutor;

  @Inject // Injected by CDI, not Spring
  @SuppressWarnings("SpringJavaAutowiringInspection")
  private EventBus eventBus;

  @Inject
  private StaticContentReader staticContentReader;

  public EngRootResource() {
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getDefaultPage() throws Exception {
    URI uri = uriInfo.getBaseUriBuilder().path("/app").build();
    return Response.seeOther(uri).build();
  }

  @Path("/app")
  public AppResource getAppResource() {
    return new AppResource(staticContentReader, uriInfo);
  }

  @Path("/api/v1/client")
  public ClientResourceV1 getClientResource() {
    return new ClientResourceV1(executionManager, domainKernel, notificationKernel, eventBus);
  }

  @Path("/api/v1/admin")
  public AdminResourceV1 getAdminResource() {
    return new AdminResourceV1(executionManager, domainKernel, notificationKernel, receiverExecutor, processorExecutor, eventBus);
  }

  @GET
  @Path("/api/v1/status")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.APPLICATION_JSON)
  public SystemStatus getStatus() {
    return new SystemStatus(
        receiverExecutor.getExecutorStatus(),
        processorExecutor.getExecutorStatus()
    );
  }
}
