package org.tiogasolutions.notify.engine.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
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

    private final DomainKernel domainKernel;
    private final ExecutionManager executionManager;

    @Autowired
    TiogaJacksonTranslator translator;

    public RouteCatalogResourceV2(ExecutionManager executionManager, DomainKernel domainKernel) {
        this.domainKernel = domainKernel;
        this.executionManager = executionManager;
    }

    private DomainProfile getDomainProfile() {
        ExecutionContext ec = executionManager.context();
        return domainKernel.findByApiKey(ec.getApiKey());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public RouteCatalog getRouteCatalog() {
        return getDomainProfile().getRouteCatalog();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public RouteCatalog putRouteCatalog(String json) {

        RouteCatalog routeCatalog;

        try {
            routeCatalog = translator.fromJson(RouteCatalog.class, json);

        } catch (Exception e) {
            log.error("Unexpected exception", e);
            String msg = (e.getMessage() == null) ? e.getClass().getName() : e.getMessage();
            throw ApiException.badRequest(msg);
        }

        // TODO - we need to dump the cache and force a reload
        DomainProfile returnProfile = domainKernel.updateRouteCatalog(getDomainProfile(), routeCatalog);
        return returnProfile.getRouteCatalog();
    }
}
