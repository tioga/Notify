package org.tiogasolutions.notify.kernel.config;

public class CouchEnvironment {

  private boolean testing = false;

  public CouchEnvironment(boolean testing) {
    this.testing = testing;
  }

  public boolean isTesting() {
    return testing;
  }
}
