package org.tiogasolutions.notify.kernel;

import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.tiogasolutions.lib.hal.*;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.tiogasolutions.notify.kernel.Paths.$api_v2_admin;
import static org.tiogasolutions.notify.kernel.Paths.$domains;

public class PubUtils {

    private final UriInfo uriInfo;
    private boolean excludeLinks;

    public PubUtils(UriInfo uriInfo) {
        this.uriInfo = uriInfo;
    }

    public PubUtils(ContainerRequestContext requestContext) {
        this.uriInfo = requestContext.getUriInfo();
    }

    public UriInfo getUriInfo() {
        return uriInfo;
    }

    public Response.ResponseBuilder toResponse(HalItem item) {

        int statusCode = item.getHttpStatusCode().getCode();

        Response.ResponseBuilder builder = Response
                .status(statusCode)
                .entity(item);

        if (statusCode == HttpStatusCode.CREATED.getCode()) {
            HalLink link = item.get_links().getLink("self");
            builder.location(link.getHref());
        }

        for (Map.Entry<String, HalLink> entry : item.get_links().entrySet()) {
            String rel = entry.getKey();
            HalLink link = entry.getValue();
            builder.link(link.getHref(), rel);
        }

        return builder;
    }

    public HalItem fromDomainProfile(HttpStatusCode statusCode, DomainProfile profile) {

        HalLinks links = HalLinks.builder()
                .create("self", uriAdminDomain(profile.getDomainName()))
                .create("domains", uriAdminDomains())
                .build();

        return new HalItemWrapper<>(profile, statusCode, links);
    }

    public HalItem fromDomainProfileResults(HttpStatusCode statusCode, List<DomainProfile> profiles) {

        HalLinks links = HalLinks.builder()
                .create("self", uriAdminDomains())
                .build();

        List<HalItem> items = new ArrayList<>();
        for (DomainProfile profile : profiles) {
            HalItem item = fromDomainProfile(null, profile);
            items.add(item);
        }

        QueryResult<HalItem> itemResults = ListQueryResult.newComplete(HalItem.class, items);

        return new HalItemWrapper<>(itemResults, statusCode, links);
    }

    public boolean isExcludeLinks() {
        return excludeLinks;
    }

    public void setExcludeLinks(boolean excludeLinks) {
        this.excludeLinks = excludeLinks;
    }

    public String uriAdmin() {
        return uriInfo.getBaseUriBuilder().path($api_v2_admin).toString();
    }

    public String uriAdminDomains() {
        return uriInfo.getBaseUriBuilder().path($api_v2_admin).path($domains).toString();
    }

    public String uriAdminDomain(String domainName) {
        return uriInfo.getBaseUriBuilder()
                .path($api_v2_admin)
                .path($domains)
                .path(domainName)
                .toString();
    }

    public Response.ResponseBuilder toAdmin() {
        HalLinks links = HalLinksBuilder.builder()
                .create("self", uriAdmin())
                .create("domains", uriAdminDomains())
                .build();

        HalItem item = new HalItem(HttpStatusCode.OK, links);

        return Response.ok(item);
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
