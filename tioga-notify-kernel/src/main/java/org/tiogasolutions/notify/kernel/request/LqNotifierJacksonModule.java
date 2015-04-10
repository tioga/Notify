package org.tiogasolutions.notify.kernel.request;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.tiogasolutions.notify.notifier.request.LqExceptionInfo;
import org.tiogasolutions.notify.notifier.request.LqRequest;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 11:45 PM
 */
public class LqNotifierJacksonModule extends SimpleModule {

  public LqNotifierJacksonModule() {
  }

  @Override
  public void setupModule(SetupContext context) {
    context.setMixInAnnotations(LqRequest.class, LqRequestMixin.class);
    context.setMixInAnnotations(LqExceptionInfo.class, LqExceptionInfoMixin.class);
  }
}

