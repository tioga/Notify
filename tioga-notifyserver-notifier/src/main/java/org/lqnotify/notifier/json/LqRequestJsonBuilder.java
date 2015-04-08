package org.lqnotify.notifier.json;

import org.lqnotify.notifier.request.LqExceptionInfo;
import org.lqnotify.notifier.request.LqRequest;
import org.lqnotify.notifier.request.LqRequestStatus;

import java.util.Iterator;
import java.util.Map;

import static java.lang.String.*;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 10:40 PM
 */
public class LqRequestJsonBuilder {

  private final StringBuilder sb = new StringBuilder();
  private String indent = "";
  private LqRequestStatus status;

  public String toJson(LqRequest request, LqRequestStatus status) {
    beginObject();

    // Basic fields
    if (status == null) {
      firstField("topic", request.getTopic());

    } else {
      firstField("requestStatus", status.name());
      field("topic", request.getTopic());
    }

    field("summary", request.getSummary());
    field("trackingId", request.getTrackingId());
    field("createdAt", request.getCreatedAt().toString());

    // Traits
    traits(request.getTraitMap());

    // Exception
    if (request.getExceptionInfo() != null) {
      exceptionInfo(true, request.getExceptionInfo());
    }

    endObject();
    return sb.toString();
  }

  protected void traits(Map<String, String> traitMap) {
    sb.append(format(",%n%s\"traitMap\" : ", indent));

    beginObject();

    Iterator<Map.Entry<String, String>> it = traitMap.entrySet().iterator();
    Map.Entry<String, String> entry;
    if (it.hasNext()) {
      entry = it.next();
      firstField(entry.getKey(), entry.getValue());
    }
    while (it.hasNext()) {
      entry = it.next();
      field(entry.getKey(), entry.getValue());
    }

    endObject();
  }

  protected void exceptionInfo(boolean first, LqExceptionInfo exInfo) {
    if (first) {
      sb.append(format(",%n%s\"exceptionInfo\" : ", indent));
    } else {
      sb.append(format(",%n%s\"cause\" : ", indent));
    }

    beginObject();
    firstField("exceptionType", exInfo.getExceptionType());
    field("message", exInfo.getMessage());

    // Stacktrace, need to escape tab and newline.
    String stackTrace = exInfo.getStackTrace().replace("\t", "\\t").replace("\n", "\\n");
    field("stackTrace", stackTrace);

    if (exInfo.getCause() == null) {
      sb.append(format(",%n%s\"cause\" : null", indent));
    } else {
      exceptionInfo(false, exInfo.getCause());
    }

    endObject();
  }

  protected void beginObject() {
    sb.append(format("{%n"));
    indent += "  ";
  }

  protected void endObject() {
    indent = indent.substring(2);
    sb.append(format("%n%s}", indent));
  }

  protected void firstField(String key, String value) {
    if (value == null) {
      sb.append(format("%s\"%s\" : null", indent, key));
    } else {
      sb.append(format("%s\"%s\" : \"%s\"", indent, key, value));
    }
  }

  protected void field(String key, String value) {
    if (value == null) {
      sb.append(format(",%n%s\"%s\" : null", indent, key));
    } else {
      sb.append(format(",%n%s\"%s\" : \"%s\"", indent, key, value));
    }
  }

  protected void field(String key, int value) {
    sb.append(format(",%n%s\"%s\" : %d", indent, key, value));
  }
}
