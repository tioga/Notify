package org.tiogasolutions.notify.kernel.config;

public class SystemConfiguration {

    private final String accessControlAllowOrigin;
    private final String clientContext;
    private final String adminContext;

    public SystemConfiguration(String accessControlAllowOrigin, String clientContext, String adminContext) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        this.clientContext = clientContext;
        this.adminContext = adminContext;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public String getClientContext() {
        return clientContext;
    }

    public String getAdminContext() {
        return adminContext;
    }
}
