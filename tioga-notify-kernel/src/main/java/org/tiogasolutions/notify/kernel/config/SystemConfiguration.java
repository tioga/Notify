package org.tiogasolutions.notify.kernel.config;

public class SystemConfiguration {

    private final String accessControlAllowOrigin;
    private final String clientContext;
    private final String adminContext;
    private final boolean autoAuthAdmin;

    public SystemConfiguration(String accessControlAllowOrigin, String clientContext, String adminContext, boolean autoAuthAdmin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        this.clientContext = clientContext;
        this.adminContext = adminContext;
        this.autoAuthAdmin = autoAuthAdmin;
    }

    public SystemConfiguration(String accessControlAllowOrigin, String clientContext, String adminContext) {
        this(accessControlAllowOrigin, clientContext, adminContext, false);
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

    public boolean isAutoAuthAdmin() {
        return autoAuthAdmin;
    }
}
