package org.tiogasolutions.notify.notifier.send;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This class is NOT thread safe
 * User: Harlan
 * Date: 1/27/2015
 * Time: 10:40 PM
 */
public class SendNotificationRequestJsonBuilder {

  private final StringBuilder sb = new StringBuilder();
  private String indent = "";
  private boolean firstElement;

  public String toJson(SendNotificationRequest request, SendNotificationRequest.Status status) {
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

    // Links
    links(request.getLinks());

    // Exception
    if (request.getExceptionInfo() != null) {
      exceptionInfo(true, request.getExceptionInfo());
    }

    endObject();
    return sb.toString();
  }

  protected void traits(Map<String, String> traitMap) {
    sb.append(String.format(",%n%s\"traitMap\" : ", indent));

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

  protected void links(List<NotificationLink> links) {
    sb.append(String.format(",%n%s\"links\" : ", indent));

    beginArray();

    for(NotificationLink link : links) {
      nextElement();
      beginObject();
      firstField("name", link.getName());
      field("href", link.getHref());
      endObject();
    }

    endArray();
  }

  protected void exceptionInfo(boolean first, NotificationExceptionInfo exInfo) {
    if (first) {
      sb.append(String.format(",%n%s\"exceptionInfo\" : ", indent));
    } else {
      sb.append(String.format(",%n%s\"cause\" : ", indent));
    }

    beginObject();
    firstField("exceptionType", exInfo.getExceptionType());
    field("message", exInfo.getMessage());

    // Stacktrace, need to escape tab and newline.
    String stackTrace = exInfo.getStackTrace().replace("\t", "\\t").replace("\n", "\\n");
    field("stackTrace", stackTrace);

    if (exInfo.getCause() == null) {
      sb.append(String.format(",%n%s\"cause\" : null", indent));
    } else {
      exceptionInfo(false, exInfo.getCause());
    }

    endObject();
  }

  protected void beginObject() {
    sb.append(String.format("{%n"));
    indent += "  ";
  }

  protected void endObject() {
    indent = indent.substring(2);
    sb.append(String.format("%n%s}", indent));
  }

  protected void beginArray() {
    indent += "  ";
    sb.append(String.format("[%n%s", indent));
    firstElement = true;
  }

  protected void nextElement() {
    if (!firstElement) {
      sb.append(String.format(",%n%s", indent));
    }
    firstElement = false;
  }

  protected void endArray() {
    indent = indent.substring(2);
    sb.append(String.format("%n%s]", indent));
    firstElement = false;
  }

  protected void firstField(String key, String value) {
    if (value == null) {
      sb.append(String.format("%s\"%s\" : null", indent, key));
    } else {
      sb.append(String.format("%s\"%s\" : \"%s\"", indent, key, value));
    }
  }

  protected void field(String key, String value) {
    if (value == null) {
      sb.append(String.format(",%n%s\"%s\" : null", indent, key));
    } else {
      sb.append(String.format(",%n%s\"%s\" : \"%s\"", indent, key, value));
    }
  }

  protected void field(String key, int value) {
    sb.append(String.format(",%n%s\"%s\" : %d", indent, key, value));
  }
}
