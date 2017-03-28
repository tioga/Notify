package org.tiogasolutions.notify.engine.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.notify.kernel.config.SystemConfiguration;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * This is the "global" filter for the Engine. It's primary responsibility is
 * for managing the execution context over the entire lifecycle of a request.
 *
 * All other filters should be processed after this one.
 */
@Provider
@PreMatching
@Priority(Priorities.USER)
public class EngineResponseFilter implements ContainerResponseFilter {

  private static final Logger log = LoggerFactory.getLogger(EngineResponseFilter.class);

  @Autowired // Injected by CDI, not Spring
  private ExecutionManager executionManager;

  @Autowired
  private SystemConfiguration systemConfiguration;

  public EngineResponseFilter() {
    log.info("Created");
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    executionManager.clearContext();
    responseContext.getHeaders().add("Access-Control-Allow-Origin", systemConfiguration.getAccessControlAllowOrigin());
    responseContext.getHeaders().add("Access-Control-Allow-Headers", "Accept, Content-Type, Authorization, Access-Control-Allow-Origin");
    responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, DELETE, PUT, POST");
    responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
  }
}
