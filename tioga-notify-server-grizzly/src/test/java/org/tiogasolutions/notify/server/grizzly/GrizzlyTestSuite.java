package org.tiogasolutions.notify.server.grizzly;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class GrizzlyTestSuite {

    @BeforeSuite
    public void beforeSuite() {
        LogbackUtils.initLogback(Level.WARN);
    }

}
