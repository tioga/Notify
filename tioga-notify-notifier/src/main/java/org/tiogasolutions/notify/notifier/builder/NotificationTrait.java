package org.tiogasolutions.notify.notifier.builder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 5:51 PM
 */
public class NotificationTrait {

    private final String key;
    private final String value;

    public NotificationTrait(String keyValue) {
        keyValue = (keyValue == null) ? null : keyValue.trim().toLowerCase();
        if (keyValue == null || keyValue.length() == 0) {
            this.key = "none";
            this.value = null;
            return;
        }
        int splitAt = keyValue.indexOf(':');
        if (splitAt > 0) {
            this.key = keyValue.substring(0, splitAt);
            if (splitAt < keyValue.length()) {
                this.value = keyValue.substring(splitAt + 1);
            } else {
                this.value = null;
            }
        } else {
            this.key = keyValue;
            this.value = null;
        }
    }

    public NotificationTrait(String key, String value) {
        this.key = (key != null) ? key.toLowerCase() : "none";
        this.value = value;
    }

    public static List<NotificationTrait> toTraits(Map<String, String> traitMap) {
        List<NotificationTrait> traits = new ArrayList<>();
        if (traitMap == null) {
            return traits;
        }
        for (Map.Entry<String, String> entry : traitMap.entrySet()) {
            traits.add(new NotificationTrait(entry.getKey(), entry.getValue()));
        }
        return traits;
    }

    public static List<NotificationTrait> toTraits(String... keyValues) {
        return toTraits(Arrays.asList(keyValues));
    }

    public static Map<String, String> toTraitMap(String... keyValues) {
        return toTraitMap(toTraits(Arrays.asList(keyValues)));
    }

    public static List<NotificationTrait> toTraits(Collection<String> keyValuePairs) {
        List<NotificationTrait> traits = new ArrayList<>();
        if (keyValuePairs == null) {
            return traits;
        }
        traits.addAll(keyValuePairs.stream().map(NotificationTrait::new)
                .collect(Collectors.toList()));
        return traits;
    }

    public static Map<String, String> toTraitMap(Collection<NotificationTrait> traits) {
        Map<String, String> traitMap = new HashMap<>();
        if (traits == null) {
            return traitMap;
        }

        for (NotificationTrait trait : traits) {
            traitMap.put(trait.getKey(), trait.getValue());
        }

        return traitMap;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
