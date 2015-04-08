package org.lqnotify.kernel.receiver;

import org.lqnotify.pub.DomainProfile;

/**
 * Created by harlan on 2/14/15.
 */
public interface RequestReceiver {
  public void receiveRequests(DomainProfile domainProfile);
}
