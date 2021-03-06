package org.tiogasolutions.notify.kernel.task;

import java.util.HashMap;
import java.util.Map;

public class TaskProcessorType {

    private static Map<String, TaskProcessorType> map = new HashMap<>();
    private final String code;

    public TaskProcessorType(String code) {
        this.code = code;
        map.put(code, this);
    }

    public static boolean isValueOf(String code) {
        return map.containsKey(code);
    }

    public static boolean isNotValueOf(String code) {
        return map.containsKey(code) == false;
    }

    /**
     * Returns the processor type for the specified code.
     *
     * @param code the processor type' code.
     * @return the value.
     * @throws IllegalArgumentException if the specified code does a known processor type.
     */
    public static TaskProcessorType valueOf(String code) throws IllegalArgumentException {
        if (map.containsKey(code) == false) {
            String msg = String.format("The processor type \"%s\" was not found.%n", code);
            throw new IllegalArgumentException(msg);
        }
        return map.get(code);
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskProcessorType that = (TaskProcessorType) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public String toString() {
        return code;
    }
}
