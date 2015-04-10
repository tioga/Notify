package org.tiogasolutions.notify.kernel.receiver;

import org.tiogasolutions.notify.pub.DomainProfile;

/**
 * Created by harlan on 2/14/15.
 */
public interface RequestReceiver {
  public void receiveRequests(DomainProfile domainProfile);
}
