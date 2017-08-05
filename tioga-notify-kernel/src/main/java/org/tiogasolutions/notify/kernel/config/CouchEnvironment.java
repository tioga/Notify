package org.tiogasolutions.notify.kernel.config;

public class CouchEnvironment {

    private boolean testing = false;

    public CouchEnvironment() {
    }

    public boolean isTesting() {
        return testing;
    }

    public CouchEnvironment setTesting(boolean testing) {
        this.testing = testing;
        return this;
    }
}
