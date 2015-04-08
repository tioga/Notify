package org.lqnotify.kernel;

import org.lqnotify.kernel.request.LqRequestEntity;

public interface RequestEventListener {

  void requestCreated(String domainName, LqRequestEntity request);

}
