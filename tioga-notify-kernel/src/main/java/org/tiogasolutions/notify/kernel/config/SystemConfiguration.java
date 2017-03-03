package org.tiogasolutions.notify.kernel.config;

public class SystemConfiguration {

    private final String accessControlAllowOrigin;
    private final String adminContext;
    private final boolean autoAuthAdmin;

    public SystemConfiguration(String accessControlAllowOrigin, String adminContext, boolean autoAuthAdmin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        this.adminContext = adminContext;
        this.autoAuthAdmin = autoAuthAdmin;
    }

    public SystemConfiguration(String accessControlAllowOrigin, String adminContext) {
        this(accessControlAllowOrigin, adminContext, false);
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public String getAdminContext() {
        return adminContext;
    }

    public boolean isAutoAuthAdmin() {
        return autoAuthAdmin;
    }
}
