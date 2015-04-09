package org.tiogasolutions.notifyserver.engine.core.v1;

import org.tiogasolutions.notifyserver.kernel.domain.DomainKernel;
import org.tiogasolutions.notifyserver.kernel.execution.ExecutionContext;
import org.tiogasolutions.notifyserver.kernel.execution.ExecutionManager;
import org.tiogasolutions.notifyserver.pub.DomainProfile;
import org.tiogasolutions.notifyserver.pub.route.RouteCatalog;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class RouteCatalogResourceV1 {

  private final DomainKernel domainKernel;
  private final ExecutionManager executionManager;

  public RouteCatalogResourceV1(ExecutionManager executionManager, DomainKernel domainKernel) {
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
