package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.exceptions.ApiBadRequestException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class RouteCatalogResourceV2 {

    private static final Logger log = LoggerFactory.getLogger(RouteCatalogResourceV2.class);

    private final ExecutionManager executionManager;

    public RouteCatalogResourceV2(ExecutionManager executionManager) {
        this.executionManager = executionManager;
    }

    private DomainProfile getDomainProfile() {
        ExecutionContext ec = executionManager.context();
        return executionManager.getDomainKernel().findByApiKey(ec.getApiKey());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RouteCatalog getRouteCatalog() {
        return getDomainProfile().getRouteCatalog();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public RouteCatalog putRouteCatalog(String json) throws IOException {
        ExceptionUtils.assertNotZeroLength(json, "route-catalog", ApiBadRequestException.class, ApiBadRequestException.class);

        RouteCatalog routeCatalog = executionManager.getObjectMapper().readValue(json, RouteCatalog.class);

        // TODO - we need to dump the cache and force a reload
        DomainProfile returnProfile = executionManager.getDomainKernel().updateRouteCatalog(getDomainProfile(), routeCatalog);
        return returnProfile.getRouteCatalog();
    }
}
