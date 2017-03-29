package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.net.HttpStatusCode;
import org.tiogasolutions.lib.hal.HalItem;
import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class AdminDomainsResourceV2 {

    private static final Logger log = LoggerFactory.getLogger(AdminDomainsResourceV2.class);

    private final PubUtils pubUtils;
    private final ExecutionManager em;

    public AdminDomainsResourceV2(PubUtils pubUtils, ExecutionManager em) {
        log.info("Created");
        this.pubUtils = pubUtils;
        this.em = em;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDomainProfiles() {
        List<DomainProfile> domainProfiles = em.getDomainKernel().listActiveDomainProfiles();

        HalItem item = pubUtils.fromDomainProfileResults(HttpStatusCode.OK, domainProfiles);
        return pubUtils.toResponse(item).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDomainWithForm(@FormParam("domainName") String domainName) {
        if (StringUtils.isBlank(domainName)) {
            throw ApiException.badRequest("The domain name must be specified.");
        }

        DomainProfile domainProfile = em.getDomainKernel().createDomain(domainName);
        HalItem item = pubUtils.fromDomainProfile(HttpStatusCode.CREATED, domainProfile);
        return pubUtils.toResponse(item).build();
    }

    @Path("/{domainName}")
    public AdminDomainResourceV2 getAdminDomainResourceV1(@PathParam("domainName") String domainName) {
        return new AdminDomainResourceV2(pubUtils, em, domainName);
    }
}
