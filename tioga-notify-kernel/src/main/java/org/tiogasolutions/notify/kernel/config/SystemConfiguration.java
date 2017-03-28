package org.tiogasolutions.notify.kernel.config;

public class SystemConfiguration {

    private final String accessControlAllowOrigin;
    private final boolean autoAuthAdmin;

    public SystemConfiguration(String accessControlAllowOrigin, boolean autoAuthAdmin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        this.autoAuthAdmin = autoAuthAdmin;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public boolean isAutoAuthAdmin() {
        return autoAuthAdmin;
    }
}
