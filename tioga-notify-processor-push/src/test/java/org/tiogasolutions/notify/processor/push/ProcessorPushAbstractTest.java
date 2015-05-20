
package org.tiogasolutions.notify.processor.push;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * User: harlan
 * Date: 10/28/13
 * Time: 10:05 PM
 */
@ContextConfiguration({"/config/spring-test-notify-processor-push.xml"})
@ActiveProfiles({"test"})
public abstract class ProcessorPushAbstractTest extends AbstractTestNGSpringContextTests {

}
