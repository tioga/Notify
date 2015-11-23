package org.tiogasolutions.notify.engine.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.lib.jaxrs.jackson.JacksonReaderWriterProvider;
import org.tiogasolutions.notify.NotifyObjectMapper;
import org.tiogasolutions.notify.kernel.domain.DomainProfileEntity;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainSummary;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.route.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotifyReaderWriterProvider extends JacksonReaderWriterProvider {

  @Autowired
  @SuppressWarnings("unchecked")
  public NotifyReaderWriterProvider(NotifyObjectMapper notifyObjectMapper) {
    super(notifyObjectMapper, MediaType.APPLICATION_JSON_TYPE);

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
}
