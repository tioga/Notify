package org.tiogasolutions.notify.extras.pwsmon;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
@Priority(1)
public class JerseySpringWorkaroundFilter extends RequestContextFilter {

  @Inject
  public JerseySpringWorkaroundFilter(ServiceLocator locator) {
    super(locator);
  }

}
