package org.tiogasolutions.notifyserver.kernel.admin;

import org.tiogasolutions.notifyserver.kernel.config.TrustedUserStore;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.NotAuthorizedException;

@Named
public class AdminKernel {

  private TrustedUserStore trustedUserStore;

  @Inject
  public AdminKernel(TrustedUserStore trustedUserStore) {
    this.trustedUserStore = trustedUserStore;
  }

  public void authorize(String username, String password) throws NotAuthorizedException {
    if (trustedUserStore.containsUser(username) == false || !trustedUserStore.isPasswordMatch(username, password)) {
      throw new NotAuthorizedException("ADMIN");
    }
  }
}
