package org.tiogasolutions.notify.engine.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.route.*;
import org.tiogasolutions.notify.kernel.jackson.NotifyKernelJacksonModule;
import org.tiogasolutions.notify.kernel.domain.DomainProfileEntity;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Collections;

public class EngineReaderWriterProvider extends JacksonReaderWriterProvider {

  public EngineReaderWriterProvider() {
    super(newObjectMapper(), MediaType.APPLICATION_JSON_TYPE);

    addSupportedType(ApiNotFoundException.class);

    addSupportedType(ListQueryResult.class);
    addSupportedType(NotificationRequest.class);
    addSupportedType(Notification.class);
    addSupportedType(ExceptionInfo.class);
    addSupportedType(CreateNotification.class);
    addSupportedType(DomainProfile.class);
    addSupportedType(DomainProfileEntity.class);
    addSupportedType(RouteCatalog.class);
    addSupportedType(Route.class);
    addSupportedType(Destination.class);
    addSupportedType(RouteDef.class);
    addSupportedType(DestinationDef.class);
    addSupportedType(SystemStatus.class);
    addSupportedType(DomainSummary.class);
  }

  public static TiogaJacksonObjectMapper newObjectMapper() {
    TiogaJacksonObjectMapper objectMapper = new TiogaJacksonObjectMapper(
          Arrays.asList(
            new TiogaJacksonModule(),
            new NotifyKernelJacksonModule()
          ),
          Collections.emptyList()
    );
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }
}
