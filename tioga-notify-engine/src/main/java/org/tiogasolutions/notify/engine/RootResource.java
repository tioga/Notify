package org.tiogasolutions.notify.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.tiogasolutions.notify.engine.v2.AdminResourceV2;
import org.tiogasolutions.notify.engine.v2.ClientResourceV2;
import org.tiogasolutions.notify.engine.web.SystemStatus;
import org.tiogasolutions.notify.engine.web.readers.StaticContentReader;
import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;

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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static org.tiogasolutions.notify.kernel.Paths.*;

@Path($root)
@Scope(value = "prototype")
public class RootResource {

    private static final Logger log = LoggerFactory.getLogger(RootResource.class);
    // Hammered by AWS for status checks, we don't want to have to re-process this code every few milliseconds.
    private static final String indexHtml;

    static {
        String html = null;
        String since = ZonedDateTime
                .now(ZoneId.of(ZoneId.SHORT_IDS.get("PST")))
                .format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm:ss a zzz"));

        try {
            Attributes attributes = getManifest().getMainAttributes();
            String version = attributes.getValue("Implementation-Version");
            String build = attributes.getValue("Build-Number");
            String timestamp = attributes.getValue("Build-Timestamp");

            html = String.format("<html><body><h1>Notify Server</h1>" +
                    "<div>Build-Number: %s</div>" +
                    "<div>Build-Timestamp: %s</div>" +
                    "<div>Implementation-Version: %s</div>" +
                    "<div>Since: %s</div>" +
                    "</body></html>", build, timestamp, version, since);

        } catch (Exception e) {
            html = String.format("<html><body>" +
                    "<h1>Notify Server</h1>" +
                    "<div>Since: %s</div>" +
                    "<div>%s</div>" +
                    "</body></html>", since, e.getMessage());
        } finally {
            indexHtml = html;
        }
    }

    @Context
    UriInfo uriInfo;
    @Autowired
    private ExecutionManager em;
    @Autowired
    private StaticContentReader staticContentReader;

    public RootResource() {
        log.debug("Created");
    }

    private static Manifest getManifest() throws IOException {
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
    @Produces(MediaType.TEXT_HTML)
    public String getIndexHtml() throws IOException {
        return healthCheck();
    }

    @GET
    @Path($health_check)
    @Produces(MediaType.TEXT_HTML)
    public String healthCheck() {
        return indexHtml;
    }

    @Path($app)
    public AppResource getAppResource() {
        return new AppResource(staticContentReader, uriInfo);
    }

    @Path($static)
    public StaticResources getIconsResource() {
        return new StaticResources(staticContentReader, uriInfo);
    }

    @Path($api_v2)
    public ClientResourceV2 getClientResource() {
        return new ClientResourceV2(em);
    }

    @Path($api_v2_admin)
    public AdminResourceV2 getAdminResource() {
        return new AdminResourceV2(newPubUtils(), em);
    }

    private PubUtils newPubUtils() {
        return new PubUtils(uriInfo);
    }

    @GET
    @Path($api_v2_status)
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public SystemStatus getStatus() {
        return new SystemStatus(
                em.getReceiverExecutor().getExecutorStatus(),
                em.getProcessorExecutor().getExecutorStatus()
        );
    }

    @GET
    @Path("/manager/status")
    public Response managerStatus() throws Exception {
        return Response.status(404).build();
    }

    @GET
    @Path("{resource: ([^\\s]+(\\.(?i)(php|PHP))$) }")
    public Response renderTXTs() throws Exception {
        return Response.status(404).build();
    }

    @GET
    @Path("/favicon.ico")
    public Response favicon_ico() {
        return Response.status(404).build();
    }

    @GET
    @Path("/trafficbasedsspsitemap.xml")
    public Response trafficbasedsspsitemap_xml() {
        return Response.status(404).build();
    }

    @GET
    @Path("/apple-touch-icon-precomposed.png")
    public Response apple_touch_icon_precomposed_png() {
        return Response.status(404).build();
    }

    @GET
    @Path("/apple-touch-icon.png")
    public Response apple_touch_icon_png() {
        return Response.status(404).build();
    }
}
