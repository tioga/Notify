package org.tiogasolutions.notify.pub.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by harlan on 5/22/15.
 */
public final class TraitInfo {
  private final String key;
  private final long count;

  @JsonCreator
  public TraitInfo(@JsonProperty("key") String key,
                   @JsonProperty("count") long count) {
    this.key = key;
    this.count = count;
  }

  public String getKey() {
    return key;
  }

  public long getCount() {
    return count;
  }
}
