
package org.lqnotify.kernel;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

/**
 * User: harlan
 * Date: 10/28/13
 * Time: 10:05 PM
 */
@ContextConfiguration({"/config/spring-test-lq-kernel.xml"})
@ActiveProfiles({"test"})
public abstract class KernelAbstractTest extends AbstractTestNGSpringContextTests {

}
