package org.tiogasolutions.notify.kernel.request;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.tiogasolutions.notify.notifier.request.NotificationExceptionInfo;
import org.tiogasolutions.notify.notifier.request.NotificationRequest;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 11:45 PM
 */
public class NotifierJacksonModule extends SimpleModule {

  public NotifierJacksonModule() {
  }

  @Override
  public void setupModule(SetupContext context) {
    context.setMixInAnnotations(NotificationRequest.class, NotificationRequestMixin.class);
    context.setMixInAnnotations(NotificationExceptionInfo.class, ExceptionInfoMixin.class);
  }
}

