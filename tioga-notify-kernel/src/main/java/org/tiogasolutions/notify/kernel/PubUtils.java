package org.tiogasolutions.notify.kernel;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.pub.PubItem;
import org.tiogasolutions.pub.PubLink;
import org.tiogasolutions.pub.PubStatus;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public class PubUtils {

    private boolean excludeLinks;

    private final UriInfo uriInfo;

    public PubUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public PubUtils(ContainerRequestContext requestContext) {
        this.uriInfo = requestContext.getUriInfo();
    }

    public Response.ResponseBuilder toResponse(PubItem pubItem) {
        Response.ResponseBuilder builder = Response.status(pubItem.getStatus().getCode()).entity(pubItem);

        for (PubLink link : pubItem.get_links().values()) {
            builder.link(link.getHref(), link.getRel());
        }

        return builder;
    }

    public boolean isExcludeLinks() {
        return excludeLinks;
    }

    public void setExcludeLinks(boolean excludeLinks) {
        this.excludeLinks = excludeLinks;
    }

    private PubStatus toStatus(HttpStatusCode statusCode) {
        return statusCode == null ? null : new PubStatus(statusCode);
    }

//    public DomainProfile toDomainProfile(HttpStatusCode code, DomainProfileEntity entity) {
//      return new DomainProfile(
//              toStatus(code),
//              PubLinks.empty(),
//              entity.getProfileId(),
//              entity.getRevision(),
//              entity.getDomainName(),
//              entity.getDomainStatus(),
//              entity.getApiKey(),
//              entity.getApiPassword(),
//              entity.getNotificationDbName(),
//              entity.getRequestDbName(),
//              entity.getRouteCatalog());
//    }
}
