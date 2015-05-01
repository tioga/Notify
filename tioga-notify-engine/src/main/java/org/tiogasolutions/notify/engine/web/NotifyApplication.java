package org.tiogasolutions.notify.engine.web;

import org.tiogasolutions.lib.spring.jaxrs.TiogaSpringApplication;
import org.tiogasolutions.notify.engine.core.RootResource;
import org.tiogasolutions.notify.engine.web.writers.EmbeddedContentMessageBodyWriter;
import org.tiogasolutions.notify.engine.web.writers.ThymeleafMessageBodyWriter;

import java.util.*;

public class NotifyApplication extends TiogaSpringApplication {

  public NotifyApplication(String profile, String springFile) {
    super(profile, springFile, createProperties(), createClasses(), createSingletons());
  }

  private static Set<Object> createSingletons() {
    return Collections.emptySet();
  }

  private static Map<String,Object> createProperties() {
    Map<String,Object> properties = new HashMap<>();

    properties.put("app.admin.context", "/api/v1/admin");
    properties.put("app.client.context", "/api/v1/client");

    return properties;
  }

  private static Set<Class<?>> createClasses() {
    Set<Class<?>> classes = new HashSet<>();

    // Filters
    classes.add(LqFilter.class);
    // Resources
    classes.add(RootResource.class);
    // JAX-RS internals
    classes.add(LqReaderWriterProvider.class);
    classes.add(LqExceptionMapper.class);
    classes.add(ThymeleafMessageBodyWriter.class);
    classes.add(EmbeddedContentMessageBodyWriter.class);

    return Collections.unmodifiableSet(classes);
  }
}
