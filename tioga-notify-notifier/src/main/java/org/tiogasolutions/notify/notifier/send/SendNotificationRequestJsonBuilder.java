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

        firstField("internal", String.valueOf(request.isInternal()));

        // Basic fields
        if (status == null) {
            field("topic", request.getTopic());

        } else {
            field("requestStatus", status.name());
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
        comma();
        newLine();
        indent();
        attr("traitMap");

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
        comma();
        newLine();
        indent();
        attr("links");

        beginArray();

        for (NotificationLink link : links) {
            nextElement();
            beginObject();
            firstField("name", link.getName());
            field("href", link.getHref());
            endObject();
        }

        endArray();
    }

    protected void exceptionInfo(boolean first, NotificationExceptionInfo exInfo) {

        comma();
        newLine();
        indent();
        attr(first ? "exceptionInfo" : "cause");

        beginObject();
        firstField("exceptionType", exInfo.getExceptionType());
        field("message", exInfo.getMessage());

        // Stacktrace, need to escape tab and newline.
        field("stackTrace", exInfo.getStackTrace());

        if (exInfo.getCause() == null) {
            comma();
            newLine();
            indent();
            attr("cause");
            value(null);
        } else {
            exceptionInfo(false, exInfo.getCause());
        }

        endObject();
    }

    protected void beginObject() {
        append("{");
        newLine();
        indent += "  ";
    }

    protected void endObject() {
        indent = indent.substring(2);
        newLine();
        indent();
        append("}");
    }

    protected void beginArray() {
        indent += "  ";
        append("[");
        newLine();
        indent();
        firstElement = true;
    }

    protected void nextElement() {
        if (!firstElement) {
            comma();
            newLine();
            indent();
        }
        firstElement = false;
    }

    protected void endArray() {
        indent = indent.substring(2);
        newLine();
        indent();
        append("]");
        firstElement = false;
    }

    protected void firstField(String key, String value) {
        indent();

        attr(key);
        value(value);
    }

    protected void field(String key, String value) {
        comma();
        newLine();
        indent();

        attr(key);
        value(value);
    }

    protected void attr(String key) {
        quote();
        append(key);
        quote();

        fieldSep();
    }

    protected void value(Object object) {

        if (object == null || object instanceof Number) {
            append(object);

        } else {
            String cleaned = object
                    .toString()
                    .replace("\t", "\\t")
                    .replace("\n", "\\n")
                    .replace("\r", ""); // screw Windows

            quote();
            append(cleaned);
            quote();
        }
    }

    private void fieldSep() {
        sb.append(" : ");
    }

    protected void quote() {
        sb.append("\"");
    }

    private void comma() {
        append(",");
    }

    protected void indent() {
        sb.append(indent);
    }

    protected void newLine() {
        // sb.append(System.lineSeparator());

        // SCREW WINDOWS!
        // Just commit to new-line only.
        sb.append("\n");
    }

    protected void append(Object object) {
        if (object == null) {
            sb.append("null");
        } else {
            sb.append(object.toString());
        }
    }

    protected void append(Number number) {
        sb.append(number == null ? null : number.toString());
    }
}
