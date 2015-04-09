package org.tiogasolutions.notifyserver.kernel.receiver;

import org.tiogasolutions.notifyserver.pub.DomainProfile;

/**
 * Created by harlan on 2/14/15.
 */
public interface RequestReceiver {
  public void receiveRequests(DomainProfile domainProfile);
}
