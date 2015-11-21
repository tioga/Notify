package org.tiogasolutions.notify.processor.logger;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class LogProcessorTestSuite {

  @BeforeSuite
  public void beforeSuite() {
    LogbackUtils.initLogback(Level.WARN);
  }

}
