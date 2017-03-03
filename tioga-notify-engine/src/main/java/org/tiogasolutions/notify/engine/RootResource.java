package org.tiogasolutions.notify.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.tiogasolutions.notify.engine.v1.AdminResourceV1;
import org.tiogasolutions.notify.engine.v1.ClientResourceV1;
import org.tiogasolutions.notify.engine.web.SystemStatus;
import org.tiogasolutions.notify.engine.web.readers.StaticContentReader;
import org.tiogasolutions.notify.kernel.PubUtils;
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
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.tiogasolutions.notify.kernel.Paths.*;

@Path($root)
@Scope(value = "prototype")
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
    public String getIndexHtml() throws IOException {
        try {
            Attributes attributes = getManifest().getMainAttributes();
            String version = attributes.getValue("Implementation-Version");
            String build = attributes.getValue("Build-Number");
            String timestamp = attributes.getValue("Build-Timestamp");

            return String.format("<html><body><h1>Notify Server</h1>" +
                    "<div>Build-Number: %s</div>" +
                    "<div>Build-Timestamp: %s</div>" +
                    "<div>Implementation-Version: %s</div>" +
                    "</body></html>", build, timestamp, version);

        } catch (Exception e) {
            return String.format("<html><body><h1>Notify Server</h1><div>%s</div></body></html>", e.getMessage());
        }
    }

    private Manifest getManifest() throws IOException {
        Enumeration<URL> resources = RootResource.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            try {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                String moduleName = manifest.getMainAttributes().getValue("Module-Name");
                if ("tioga-notify-server-grizzly".equalsIgnoreCase(moduleName)) {
                    return manifest;
                }
            } catch (IOException ignored) {/*ignored*/}
        }
        throw new IOException("Manifest not found.");
    }

    @GET
    @Path($health_check)
    @Produces(MediaType.TEXT_HTML)
    public Response healthCheck$GET() {
        return Response.status(Response.Status.OK).build();
    }

    @Path($app)
    public AppResource getAppResource() {
        return new AppResource(staticContentReader, uriInfo);
    }

    @Path($api_v1)
    public ClientResourceV1 getClientResource() {
        return new ClientResourceV1(executionManager, domainKernel, notificationKernel, eventBus);
    }

    @Path($api_v1_admin)
    public AdminResourceV1 getAdminResource() {
        return new AdminResourceV1(newPubUtils(), executionManager, domainKernel, notificationKernel, receiverExecutor, processorExecutor, eventBus);
    }

    private PubUtils newPubUtils() {
        return new PubUtils(uriInfo);
    }

    @GET
    @Path($api_v1_status)
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public SystemStatus getStatus() {
        return new SystemStatus(
                receiverExecutor.getExecutorStatus(),
                processorExecutor.getExecutorStatus()
        );
    }
}
