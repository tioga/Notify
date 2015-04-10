package org.tiogasolutions.notify.engine.web;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.ws.rs.core.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class LqApplicationSupport extends Application {

  protected final Set<Class<?>> classes = new HashSet<>();
  protected final Set<Object> singletons = new HashSet<>();
  protected final Map<String, Object> properties = new HashMap<>();
  protected final ListableBeanFactory beanFactory;

  public LqApplicationSupport(String profile, String springFile, Map<String,Object> properties, Set<Class<?>> classes, Set<Object> singletons) {

    AbstractXmlApplicationContext springContext;

    if (springFile.startsWith("classpath:")) {
      springContext = new ClassPathXmlApplicationContext();
    } else {
      springContext = new FileSystemXmlApplicationContext();
    }

    springContext.setConfigLocation(springFile);
    springContext.getEnvironment().setActiveProfiles(profile);
    springContext.refresh();

    this.beanFactory = springContext;

    this.classes.addAll(classes);
    this.singletons.addAll(singletons);
    this.properties.putAll(properties);

    checkForDuplicates();
  }

  public ListableBeanFactory getBeanFactory() {
    return beanFactory;
  }

  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }

  @Override
  public Map<String, Object> getProperties() {
    return properties;
  }

  protected void checkForDuplicates() {
    Set<Class> existing = new HashSet<>();

    for (Class type : classes) {
      if (type == null) continue;
      if (existing.contains(type)) {
        String msg = String.format("The class %s has already been registered.", type.getName());
        throw new IllegalArgumentException(msg);
      }
    }

    existing.clear();
  }
}
