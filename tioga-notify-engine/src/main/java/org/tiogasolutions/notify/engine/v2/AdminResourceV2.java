package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.notify.kernel.PubUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AdminResourceV2 {

    private final PubUtils pubUtils;
    private final ExecutionManager em;

    public AdminResourceV2(PubUtils pubUtils, ExecutionManager em) {
        this.pubUtils = pubUtils;
        this.em = em;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDefaultPage() {
        return pubUtils.toAdmin().build();
    }

    @Path("/domains")
    public AdminDomainsResourceV2 getDomainProfiles() {
        return new AdminDomainsResourceV2(pubUtils, em);
    }

    @Path("/system")
    public SystemResourceV2 getSystemResourceV1() {
        return new SystemResourceV2(em);
    }
}
