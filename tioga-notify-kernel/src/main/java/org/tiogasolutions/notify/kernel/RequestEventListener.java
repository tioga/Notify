package org.tiogasolutions.notify.kernel;

import org.tiogasolutions.notify.kernel.request.LqRequestEntity;

public interface RequestEventListener {

  void requestCreated(String domainName, LqRequestEntity request);

}
