package org.tiogasolutions.notify.sender.couch;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class CouchSenderTestSuite {

    @BeforeSuite
    public void beforeSuite() {
        LogbackUtils.initLogback(Level.WARN);
    }

}
