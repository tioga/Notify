package org.tiogasolutions.notify.kernel.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.notify.kernel.config.TrustedUserStore;

import javax.ws.rs.NotAuthorizedException;

@Component
public class AdminKernel {

    private TrustedUserStore trustedUserStore;

    @Autowired
    public AdminKernel(TrustedUserStore trustedUserStore) {
        this.trustedUserStore = trustedUserStore;
    }

    public void authorize(String username, String password) throws NotAuthorizedException {
        if (trustedUserStore.containsUser(username) == false || !trustedUserStore.isPasswordMatch(username, password)) {
            throw new NotAuthorizedException("ADMIN");
        }
    }
}
