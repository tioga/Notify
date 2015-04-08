package org.lqnotify.pub.route;

import com.fasterxml.jackson.annotation.JsonValue;
import org.tiogasolutions.dev.common.ReflectUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by harlan on 2/28/15.
 */
public class ArgValue {

  public enum ArgType {STRING, NUMBER, BOOLEAN, LIST, MAP}

  private final ArgType argType;
  private final Object value;

  public ArgValue(Object value) {
    this.value = value;
    if (value == null) {
      argType = ArgType.STRING;
    } else if (value instanceof String) {
      argType = ArgType.STRING;
    } else if (value instanceof Number) {
      argType = ArgType.NUMBER;
    } else if (value instanceof Boolean) {
      argType = ArgType.BOOLEAN;
    } else if (value instanceof List) {
      argType = ArgType.LIST;
    } else if (value instanceof Map) {
      argType = ArgType.MAP;
    } else {
      throw ApiException.badRequest("Unsupported ArgType " + value.getClass());
    }
  }

  public ArgType getArgType() {
    return argType;
  }

  public boolean isEqual(Object other) {
    if (value == null) {
      return other == null;
    } else {
      return value.equals(other);
    }
  }

  @JsonValue
  public Object getValue() {
    return value;
  }

  public Object asObject() {
    return value;
  }

  public String asString() {
    return (value != null) ? value.toString() : null;
  }

  public Boolean asBoolean() {
    return (Boolean)value;
  }

  public Number asNumber() {
    return (Number)value;
  }

  @SuppressWarnings("unchecked")
  public <T> T asEnum(Class<T> type) {
    try {
      if (value == null) return null;
      Method method = ReflectUtils.getMethod(type, "valueOf", String.class);
      return (T)method.invoke(null, value.toString());

    } catch (IllegalAccessException  | InvocationTargetException e) {
      String msg = String.format("The static method valueOf(String) could not be invoked for %s.", type.getName());
      throw new UnsupportedOperationException(msg, e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<ArgValue> asList() {
    List<Object> valueList = (List<Object>)value;
    return valueList.stream()
        .map(ArgValue::new)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public Map<String, ArgValue> asMap() {
    Map<String, Object> valueMap = (Map<String, Object>)value;
    Map<String, ArgValue> argValueMap = new HashMap<>();
    for(Map.Entry<String, Object> entry : valueMap.entrySet()) {
      argValueMap.put(entry.getKey(), new ArgValue(entry.getValue()));
    }
    return argValueMap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArgValue argValue = (ArgValue) o;

    if (argType != argValue.argType) return false;
    if (value != null ? !value.equals(argValue.value) : argValue.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = argType != null ? argType.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ArgValue{" +
        "argType=" + argType +
        ", value=" + value +
        '}';
  }
}
