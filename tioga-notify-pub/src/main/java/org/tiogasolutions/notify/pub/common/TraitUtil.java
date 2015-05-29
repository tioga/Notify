package org.tiogasolutions.notify.pub.common;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 5:51 PM
 */
public class TraitUtil {

  public static Map<String, String> toTraitMap(String... keyValues) {
    Map<String, String> traitMap = new HashMap<>();

    for(String keyValue : keyValues) {
      String key;
      String value;
      keyValue = (keyValue == null) ? null : keyValue.trim().toLowerCase();
      if (keyValue == null || keyValue.length() == 0) {
        key = "none";
        value = null;
      } else {
        int splitAt = keyValue.indexOf(':');
        if (splitAt > 0) {
          key = keyValue.substring(0, splitAt);
          if (splitAt < keyValue.length()) {
            value = keyValue.substring(splitAt + 1);
          } else {
            value = null;
          }
        } else {
          key = keyValue;
          value = null;
        }
      }
      traitMap.put(key, value);
    }

    return traitMap;
  }

}
