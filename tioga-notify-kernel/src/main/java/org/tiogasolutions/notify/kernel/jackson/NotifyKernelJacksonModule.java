package org.tiogasolutions.notify.kernel.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.tiogasolutions.notify.notifier.request.NotificationExceptionInfo;
import org.tiogasolutions.notify.notifier.request.NotificationRequest;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 11:45 PM
 */
public class NotifyKernelJacksonModule extends SimpleModule {

  public NotifyKernelJacksonModule() {
  }

  @Override
  public void setupModule(SetupContext context) {
    // Register mixins which support JSON translation of classes from the notifier module.
    context.setMixInAnnotations(NotificationRequest.class, NotificationRequestMixin.class);
    context.setMixInAnnotations(NotificationExceptionInfo.class, NotificationExceptionInfoMixin.class);
  }
}

