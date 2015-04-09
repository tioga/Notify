package org.tiogasolutions.notifyserver.server.grizzly;

import org.tiogasolutions.notifyserver.engine.core.EngApplication;
import org.tiogasolutions.notifyserver.engine.core.EngRootResource;
import org.tiogasolutions.notifyserver.engine.web.LqApplicationSupport;

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
