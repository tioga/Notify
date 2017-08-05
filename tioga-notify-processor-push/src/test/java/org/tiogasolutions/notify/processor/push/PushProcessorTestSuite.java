package org.tiogasolutions.notify.processor.push;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class PushProcessorTestSuite {

    @BeforeSuite
    public void beforeSuite() {
        LogbackUtils.initLogback(Level.WARN);
    }

}
