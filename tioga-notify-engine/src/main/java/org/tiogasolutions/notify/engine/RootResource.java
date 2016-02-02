package org.tiogasolutions.notify.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.tiogasolutions.notify.engine.v1.AdminResourceV1;
import org.tiogasolutions.notify.engine.v1.ClientResourceV1;
import org.tiogasolutions.notify.engine.web.SystemStatus;
import org.tiogasolutions.notify.engine.web.readers.StaticContentReader;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.event.EventBus;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.receiver.ReceiverExecutor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorExecutor;

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
@Scope(value="prototype")
public class RootResource {

  private static final Logger log = LoggerFactory.getLogger(RootResource.class);

  @Context
  UriInfo uriInfo;

  @Autowired
  private DomainKernel domainKernel;

  @Autowired
  private ExecutionManager executionManager;

  @Autowired
  private NotificationKernel notificationKernel;

  @Autowired
  private ReceiverExecutor receiverExecutor;

  @Autowired
  private TaskProcessorExecutor processorExecutor;

  @Autowired
  private EventBus eventBus;

  @Autowired
  private StaticContentReader staticContentReader;

  public RootResource() {
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
