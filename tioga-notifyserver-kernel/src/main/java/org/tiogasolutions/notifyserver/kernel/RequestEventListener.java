package org.tiogasolutions.notifyserver.kernel;

import org.tiogasolutions.notifyserver.kernel.request.LqRequestEntity;

public interface RequestEventListener {

  void requestCreated(String domainName, LqRequestEntity request);

}
