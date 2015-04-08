package org.lqnotify.server.grizzly;

import org.lqnotify.engine.core.EngApplication;
import org.lqnotify.engine.core.EngRootResource;
import org.lqnotify.engine.web.LqApplicationSupport;

import java.util.*;

public class NotifyJaxRsConfig extends LqApplicationSupport {

  public NotifyJaxRsConfig(String profile, String springFile) {
    super(profile, springFile, createProperties(), createClasses(), createSingletons());
  }

  public static Map<String,Object> createProperties() {
    Map<String,Object> properties = new HashMap<>();

    properties.putAll(EngApplication.createProperties());

    return properties;
  }

  private static Set<Object> createSingletons() {
    Set<Object> singletons = new HashSet<>();

    singletons.addAll(EngApplication.createSingletons());

    return Collections.unmodifiableSet(singletons);
  }

  public static Set<Class<?>> createClasses() {
    Set<Class<?>> classes = new HashSet<>();

    classes.addAll(EngApplication.createClasses());

    classes.remove(EngRootResource.class);
    classes.add(EngRootResource.class);

    return Collections.unmodifiableSet(classes);
  }
}
