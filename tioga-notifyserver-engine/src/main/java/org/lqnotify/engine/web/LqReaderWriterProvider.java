package org.lqnotify.engine.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.crazyyak.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;
import org.lqnotify.kernel.request.LqNotifierJacksonModule;
import org.lqnotify.kernel.domain.DomainProfileEntity;
import org.lqnotify.kernel.notification.CreateNotification;
import org.lqnotify.notifier.request.LqRequest;
import org.lqnotify.pub.*;
import org.lqnotify.pub.route.*;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collections;

public class LqReaderWriterProvider extends JacksonReaderWriterProvider {

  public LqReaderWriterProvider() {
    super(newObjectMapper(), MediaType.APPLICATION_JSON_TYPE);

    addSupportedType(ApiNotFoundException.class);

    addSupportedType(ListQueryResult.class);
    addSupportedType(LqRequest.class);
    addSupportedType(Notification.class);
    addSupportedType(Notifications.class);
    addSupportedType(ExceptionInfo.class);
    addSupportedType(ExceptionTraceElement.class);
    addSupportedType(CreateNotification.class);
    addSupportedType(DomainProfile.class);
    addSupportedType(DomainProfileEntity.class);
    addSupportedType(RouteCatalog.class);
    addSupportedType(Route.class);
    addSupportedType(Destination.class);
    addSupportedType(RouteDef.class);
    addSupportedType(DestinationDef.class);
    addSupportedType(SystemStatus.class);
  }

  public static TiogaJacksonObjectMapper newObjectMapper() {
    TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper(
          Arrays.asList(
            new TiogaJacksonModule(),
            new LqNotifierJacksonModule()
          ),
          Collections.emptyList()
    );
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }
}
