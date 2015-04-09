package org.tiogasolutions.notifyserver.engine.core;

import org.tiogasolutions.notifyserver.engine.web.*;

import java.util.*;

public class EngApplication extends LqApplicationSupport {

  public EngApplication(String profile, String springFile) {
    super(profile, springFile, createProperties(), createClasses(), createSingletons());
  }

  public static Set<Object> createSingletons() {
    return Collections.emptySet();
  }

  public static Map<String,Object> createProperties() {
    Map<String,Object> properties = new HashMap<>();

    properties.put("app.admin.context", "/api/v1/admin");
    properties.put("app.client.context", "/api/v1/client");

    return properties;
  }

  public static Set<Class<?>> createClasses() {
    Set<Class<?>> classes = new HashSet<>();

    // Filters
    classes.add(LqFilter.class);
    // Resources
    classes.add(EngRootResource.class);
    // JAX-RS internals
    classes.add(LqReaderWriterProvider.class);
    classes.add(LqExceptionMapper.class);
    classes.add(ThymeleafMessageBodyWriter.class);
    classes.add(EmbeddedContentMessageBodyWriter.class);

    return Collections.unmodifiableSet(classes);
  }
}
