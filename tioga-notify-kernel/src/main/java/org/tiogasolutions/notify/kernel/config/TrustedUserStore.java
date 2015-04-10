package org.tiogasolutions.notify.kernel.config;

import java.util.Map;

/**
 * Created by harlan on 3/27/15.
 */
// TODO - Later may come from DB or elsewhere, may want an interface.
public class TrustedUserStore {
  private final Map<String,String> trustedUserMap;

  public TrustedUserStore(Map<String, String> trustedUserMap) {
    this.trustedUserMap = trustedUserMap;
  }

  public boolean containsUser(String username) {
    return trustedUserMap.containsKey(username);
  }

  public boolean isPasswordMatch(String userName, String password) {
    return trustedUserMap.get(userName).equals(password);
  }
}
