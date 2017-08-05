package org.tiogasolutions.notify.notifier;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class NotifyTestSuite {

    @BeforeSuite
    public void beforeSuite() {
        LogbackUtils.initLogback(Level.WARN);
    }

}
