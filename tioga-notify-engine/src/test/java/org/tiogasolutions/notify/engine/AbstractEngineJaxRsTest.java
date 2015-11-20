package org.tiogasolutions.notify.engine;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.SpringLifecycleListener;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.glassfish.jersey.test.JerseyTestNg;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.tiogasolutions.notify.engine.web.NotifyApplication;
import org.tiogasolutions.notify.kernel.config.CouchServers;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.test.TestFactory;

import javax.ws.rs.core.Application;

public class AbstractEngineJaxRsTest extends JerseyTestNg.ContainerPerClassTest {

  private TestFactory testFactory;
  private AutowireCapableBeanFactory beanFactory;
  private AbstractXmlApplicationContext applicationContext;

  @BeforeMethod
  public void autowireTest() throws Exception {
    beanFactory.autowireBean(this);
  }

  @Override
  protected Application configure() {

    applicationContext = new ClassPathXmlApplicationContext();
    applicationContext.setConfigLocation("classpath:/config/spring-test-notify-engine.xml");
    applicationContext.getEnvironment().setActiveProfiles("test");
    applicationContext.refresh();

    beanFactory = (AutowireCapableBeanFactory)applicationContext;
    testFactory = new TestFactory(getCouchServers(), getDomainKernel(), getNotificationKernel());

    NotifyApplication application = beanFactory.getBean(NotifyApplication.class);

    ResourceConfig resourceConfig = ResourceConfig.forApplication(application);
    resourceConfig.register(SpringLifecycleListener.class);
    resourceConfig.register(RequestContextFilter.class);
    resourceConfig.property("contextConfig", applicationContext);

    resourceConfig.packages("org.tiogasolutions.notify");

    return resourceConfig;
  }

  public String toHttpAuth(String username, String password) {
    return getTestFactory().toHttpAuth(username, password);
  }

  public TestFactory getTestFactory() {
    return testFactory;
  }

  public BeanFactory getBeanFactory() {
    return beanFactory;
  }

  public DomainKernel getDomainKernel() {
    return getBeanFactory().getBean(DomainKernel.class);
  }

  public NotificationKernel getNotificationKernel() {
    return getBeanFactory().getBean(NotificationKernel.class);
  }

  public CouchServers getCouchServers() {
    return getBeanFactory().getBean(CouchServers.class);
  }

  public ExecutionManager getExecutionManager() {
    return getBeanFactory().getBean(ExecutionManager.class);
  }
}
