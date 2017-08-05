package org.tiogasolutions.notify.processor.smtp;

import ch.qos.logback.classic.Level;
import org.testng.annotations.BeforeSuite;
import org.tiogasolutions.dev.common.LogbackUtils;

public class SmtpProcessorTestSuite {

    @BeforeSuite
    public void beforeSuite() {
        LogbackUtils.initLogback(Level.WARN);
    }

}
