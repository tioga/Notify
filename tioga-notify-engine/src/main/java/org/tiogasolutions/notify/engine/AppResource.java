package org.tiogasolutions.notify.engine;

import org.tiogasolutions.dev.common.net.InetMediaType;
import org.tiogasolutions.notify.engine.web.readers.StaticContentReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriInfo;

/**
 * Created by jacobp on 3/16/2015.
 */
public class AppResource {
  
  private final UriInfo uriInfo;
  private final StaticContentReader staticContentReader;
  
  public AppResource(StaticContentReader staticContentReader, UriInfo uriInfo) {
    this.staticContentReader = staticContentReader;
    this.uriInfo = uriInfo;
  }

  @GET
  @Produces(InetMediaType.TEXT_HTML_VALUE)
  public byte[] renderAppRoot() {
    return staticContentReader.readContent("index.html");
  }

  @GET
  @Produces(InetMediaType.IMAGE_PNG_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(png|PNG))$) }")
  public byte[] renderPNGs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces(InetMediaType.IMAGE_GIF_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(gif|GIF))$) }")
  public byte[] renderGIFs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces(InetMediaType.TEXT_PLAIN_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(txt|TXT|text|TEXT))$) }")
  public byte[] renderText() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces(InetMediaType.TEXT_HTML_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(html|HTML))$) }")
  public byte[] renderHtml() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces(InetMediaType.TEXT_CSS_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(css|CSS))$) }")
  public byte[] renderCSS() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces(InetMediaType.APPLICATION_JAVASCRIPT_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(js|JS))$) }")
  public byte[] renderJavaScript() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces(InetMediaType.IMAGE_ICON_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(ico|ICO))$) }")
  public byte[] renderICOs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces(InetMediaType.APPLICATION_PDF_VALUE)
  @Path("{resource: ([^\\s]+(\\.(?i)(pdf|PDF))$) }")
  public byte[] renderPDFs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces("application/font-woff")
  @Path("{resource: ([^\\s]+(\\.(?i)(otf|OTF))$) }")
  public byte[] renderOTFs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces("application/vnd.ms-fontobject")
  @Path("{resource: ([^\\s]+(\\.(?i)(eot|EOT))$) }")
  public byte[] renderEOTs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces("image/svg+xml")
  @Path("{resource: ([^\\s]+(\\.(?i)(svg|SVG))$) }")
  public byte[] renderSVGs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces("application/x-font-ttf")
  @Path("{resource: ([^\\s]+(\\.(?i)(ttf|TTF))$) }")
  public byte[] renderTTFs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }

  @GET
  @Produces("application/font-woff")
  @Path("{resource: ([^\\s]+(\\.(?i)(woff|WOFF|woff2|WOFF2))$) }")
  public byte[] renderWOFFs() throws Exception {
    return staticContentReader.readContent(uriInfo);
  }
}
