package org.tiogasolutions.notify.kernel.config;

public class CouchEnvironment {

  private final boolean testEnvironment;

  public CouchEnvironment(boolean testEnvironment) {
    this.testEnvironment = testEnvironment;
  }

  public boolean isTest() {
    return testEnvironment;
  }

  public boolean isNotTest() {
    return !testEnvironment;
  }

  public static CouchEnvironment test() {
    return new CouchEnvironment(true);
  }

  public static CouchEnvironment notTest() {
    return new CouchEnvironment(false);
  }
}
