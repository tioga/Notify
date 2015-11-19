package org.tiogasolutions.notify.kernel.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

@Component
public class ExecutionManager implements ExecutionAccessor {
  private final DomainKernel domainKernel;
  private final ThreadLocal<ExecutionContext> threadLocal = new ThreadLocal<>();

  @Autowired
  public ExecutionManager(DomainKernel domainKernel) {
    this.domainKernel = domainKernel;
  }

  public void clearContext() {
    threadLocal.remove();
  }

  public ExecutionContext newSystemContext() {
    ExecutionContext context = new ExecutionContext(domainKernel.getSystemDomain());
    assignContext(context);
    return context;
  }

  public ExecutionContext newSystemContext(UriInfo uriInfo, HttpHeaders httpHeaders) {
    ExecutionContext context = new ExecutionContext(domainKernel.getSystemDomain(), uriInfo, httpHeaders);
    assignContext(context);
    return context;
  }

  public ExecutionContext newApiContext(String apiKey, UriInfo uriInfo, HttpHeaders httpHeaders) {
    DomainProfile domainProfile = domainKernel.findByApiKey(apiKey);
    ExecutionContext context = new ExecutionContext(domainProfile, uriInfo, httpHeaders);
    assignContext(context);
    return context;
  }

  public ExecutionContext newApiContext(DomainProfile domainProfile, UriInfo uriInfo, HttpHeaders httpHeaders) {
    ExecutionContext context = new ExecutionContext(domainProfile, uriInfo, httpHeaders);
    assignContext(context);
    return context;
  }

  public ExecutionContext newApiContext(String apiKey) {
    DomainProfile domainProfile = domainKernel.findByApiKey(apiKey);
    ExecutionContext context = new ExecutionContext(domainProfile);
    assignContext(context);
    return context;
  }

  public ExecutionContext newApiContext(DomainProfile domainProfile) {
    ExecutionContext context = new ExecutionContext(domainProfile);
    assignContext(context);
    return context;
  }

  public void assignContext(ExecutionContext context) {
    threadLocal.set(context);
  }

  @Override
  public boolean hasContext() {
    return threadLocal.get() != null;
  }

  @Override
  // TODO - why is this not getContext()?
  public ExecutionContext context() {
    ExecutionContext context = threadLocal.get();
    if (context == null) {
      throw ApiException.internalServerError("There is no current execution context for this thread.");
    } else {
      return context;
    }
  }

  @Override
  public String domainName() {
    return context().getDomainName();
  }
}
