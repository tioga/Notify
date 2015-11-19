package org.tiogasolutions.notify.engine.web.writers;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.lib.thymeleaf.ClassPathTemplateResolver;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

@Provider
@Component
public class ThymeleafMessageBodyWriter implements MessageBodyWriter<Thymeleaf> {

  @Context
  UriInfo uriInfo;

  private final TemplateEngine engine;

  public ThymeleafMessageBodyWriter() {

    ClassPathTemplateResolver templateResolver = new ClassPathTemplateResolver();
    templateResolver.setTemplateMode("HTML5");
    templateResolver.setSuffix(ThymeleafViewFactory.TAIL);
    templateResolver.setPrefix(ThymeleafViewFactory.ROOT);
    templateResolver.setCacheable(false);

    engine = new TemplateEngine();
    engine.setTemplateResolver(templateResolver);
    engine.addDialect(new Java8TimeDialect());
  }

  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return Thymeleaf.class.equals(type);
  }

  @Override
  public long getSize(Thymeleaf thymeleaf, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  public String getBaseUri() {
    return uriInfo.getBaseUri().toASCIIString();
  }

  @Override
  public void writeTo(Thymeleaf thymeleaf, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
    writeTo(thymeleaf, entityStream);
  }

  /**
   * Provided mainly for testing, writes the thymeleaf to the specified writer.
   * @param thymeleaf the thymeleaf instanace to be rendered
   * @param writer the writer that the thymeleaf will be rendered to
   * @throws java.io.IOException if we are having a bad day
   */
  public void writeTo(Thymeleaf thymeleaf, Writer writer) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    writeTo(thymeleaf, out);
    String text = new String(out.toByteArray(), Charset.forName("UTF-8"));
    writer.write(text);
  }

  /**
   * Writes the thymeleaf to the specified writer.
   * @param thymeleaf the thymeleaf instanace to be rendered
   * @param outputStream the output stream that the thymeleaf will be rendered to
   * @throws java.io.IOException if we are having a bad day
   */
  public void writeTo(Thymeleaf thymeleaf, OutputStream outputStream) throws IOException {
    String view = thymeleaf.getView();

    org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
    context.setVariables(thymeleaf.getVariables());

    String baseUri = StringUtils.substring(getBaseUri(), 0, -1);
    context.setVariable("contextRoot", baseUri);

    StringWriter writer = new StringWriter();
    engine.process(view, context, writer);

    String content = writer.toString();
    outputStream.write(content.getBytes());
  }
}
