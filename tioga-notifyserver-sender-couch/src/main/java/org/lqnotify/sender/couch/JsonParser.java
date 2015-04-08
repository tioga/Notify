package org.lqnotify.sender.couch;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jacobp on 3/16/2015.
 */
public class JsonParser {

  public JsonParser() {
  }

  public Map<String,String> parse(String text) {

    int pos = text.indexOf("{");
    if (pos < 0) {
      throw new IllegalArgumentException("Unable to find starting { in JSON text.");
    }

    return split(text, 0);
  }

  protected Map<String,String> split(String text, int start) {

    int marker = 0;
    String lastKey = null;
    State state = State.none;
    Map<String,String> map = new LinkedHashMap<>();

    for (int i = start; i < text.length(); i++) {
      char curr = text.charAt(i);

      if (state.isNone()) {
        if (curr == '"') {
          marker = i+1;
          state = State.inKey;
        }
      } else if (state.isInKey()) {
        if (curr == '"') {
          lastKey = text.substring(marker, i);
          state = State.afterKey;
        }
      } else if (state.isAfterKey()) {
        if (curr == ':') {
          state = State.beforeValue;
        }
      } else if (state.isBeforeValue()) {
        if (curr == '"') {
          marker = i+1;
          state = State.inString;
        } else if (curr == '{') {
          marker = i;
          state = State.inObject;
        } else if (Character.isWhitespace(curr) == false) {
          state = State.inNumber;
        }
      } else if (state.isInString()) {
        if (curr == '\"' && text.charAt(i-1) != '\\') {
          String lastValue = text.substring(marker, i);
          map.put(lastKey, lastValue);
          state = State.afterValue;
        }
      } else if (state.isInNumber()) {
        if (curr == ',' || Character.isWhitespace(curr)) {
          String lastValue = text.substring(marker, i);
          map.put(lastKey, lastValue);
          state = State.afterValue;
        }
      } else if (state.isInObject()) {
        int brackets = 1;
        for (; i < text.length(); i++) {
          curr = text.charAt(i);
          if (curr == '{') {
            brackets++;
          } else if (curr == '}') {
            brackets--;
          }
          if (brackets == 0)  {
            String lastValue = text.substring(marker, i+1);
            map.put(lastKey, lastValue);
            state = State.afterValue;
            break;
          }
        }
      } else if (state.isAfterValue()) {
        if (curr == ',') {
          state = State.none;
        }
      }
    }

    return map;
  }

  public enum State {
    none, inKey, afterKey, beforeValue, inString, inObject, inNumber, afterValue;
    public boolean isNone() { return this == none; }
    public boolean isInKey() { return this == inKey; }
    public boolean isAfterKey() { return this == afterKey; }
    public boolean isBeforeValue() { return this == beforeValue; }
    public boolean isInString() { return this == inString; }
    public boolean isInObject() { return this == inObject; }
    public boolean isInNumber() { return this == inNumber; }
    public boolean isAfterValue() { return this == afterValue; }
  }
}
