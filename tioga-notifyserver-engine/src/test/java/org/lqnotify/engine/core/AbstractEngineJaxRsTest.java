package org.lqnotify.engine.core;

import org.crazyyak.runner.jersey.support.JerseySpringBridge;
import org.crazyyak.runner.jersey.support.ResourceConfigAdapter;
import org.glassfish.jersey.test.JerseyTestNg;
import org.lqnotify.kernel.TestFactory;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.execution.ExecutionManager;
import org.lqnotify.kernel.notification.NotificationKernel;
import org.springframework.beans.factory.BeanFactory;

import javax.ws.rs.core.Application;

public class AbstractEngineJaxRsTest extends JerseyTestNg.ContainerPerClassTest {

  private EngApplication application;
  private TestFactory testFactory;

  @Override
  protected Application configure() {
    application = new EngApplication("test", "classpath:/config/spring-test-lq-engine.xml");

    ResourceConfigAdapter adapter = new ResourceConfigAdapter(application);
    adapter.register(new JerseySpringBridge(application.getBeanFactory()));

    testFactory = new TestFactory(getDomainKernel(), getNotificationKernel());

    return adapter;
  }

  public String toHttpAuth(String username, String password) {
    return getTestFactory().toHttpAuth(username, password);
  }

  public TestFactory getTestFactory() {
    return testFactory;
  }

  public EngApplication getApplication() {
    return application;
  }

  public BeanFactory getBeanFactory() {
    return application.getBeanFactory();
  }

  public DomainKernel getDomainKernel() {
    return getBeanFactory().getBean(DomainKernel.class);
  }

  public NotificationKernel getNotificationKernel() {
    return getBeanFactory().getBean(NotificationKernel.class);
  }

  public ExecutionManager getExecutionManager() {
    return getBeanFactory().getBean(ExecutionManager.class);
  }
}
