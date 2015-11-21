package org.tiogasolutions.notify.engine;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class EngineTestSuite {

  @BeforeSuite
  public void beforeSuite() {
    LogbackUtils.initLogback(Level.WARN);
  }

}
