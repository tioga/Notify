package org.tiogasolutions.notify.kernel.config;

import org.tiogasolutions.dev.common.BeanUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by harlan on 3/27/15.
 */
// TODO - Later may come from DB or elsewhere, may want an interface.
public class TrustedUserStore {

  private final Map<String,String> trustedUserMap = new HashMap<>();

  public TrustedUserStore(Map<String, String> trustedUserMap) {
    this.trustedUserMap.putAll(trustedUserMap);
  }

  /**
   * A comma separated list of values further differentiating username and password by a comma.
   * For example, "micky:ILoveDisney, minne:ILoveMickey" would define two users, "mickey" with
   * password "ILoveDisney" and "minnie" with password "ILoveMickey"
   * @param csvTraits the trusted users.
   */
  public TrustedUserStore(String csvTraits) {
    for (String trait : csvTraits.split(",")) {
      String[] parts = trait.trim().split(":");
      this.trustedUserMap.put(parts[0], (parts.length == 1) ? null : parts[1]);
    }
  }

  public boolean containsUser(String username) {
    return trustedUserMap.containsKey(username);
  }

  public boolean isPasswordMatch(String userName, String password) {
    return trustedUserMap.get(userName).equals(password);
  }
}
