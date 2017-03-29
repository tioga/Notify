package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tiogasolutions.dev.common.exceptions.ApiBadRequestException;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public RouteCatalog putRouteCatalog(String json) {
        ExceptionUtils.assertNotZeroLength(json, "route-catalog", ApiBadRequestException.class, ApiBadRequestException.class);

        RouteCatalog routeCatalog;

        try {
            routeCatalog = executionManager.getTranslator().fromJson(RouteCatalog.class, json);

        } catch (Exception e) {
            log.error("Unexpected exception translating to JSON", e);
            String msg = (e.getMessage() == null) ? e.getClass().getName() : e.getMessage();
            throw ApiException.badRequest(msg);
        }

        // TODO - we need to dump the cache and force a reload
        DomainProfile returnProfile = executionManager.getDomainKernel().updateRouteCatalog(getDomainProfile(), routeCatalog);
        return returnProfile.getRouteCatalog();
    }
}
