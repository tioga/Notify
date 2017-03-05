package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.route.RouteCatalog;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class RouteCatalogResourceV2 {

  private final DomainKernel domainKernel;
  private final ExecutionManager executionManager;

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
  public RouteCatalog putRouteCatalog(RouteCatalog routeCatalog) {
    // TODO - we need to dump the cache and force a reload
    DomainProfile returnProfile = domainKernel.updateRouteCatalog(getDomainProfile(), routeCatalog);
    return returnProfile.getRouteCatalog();
  }
}
