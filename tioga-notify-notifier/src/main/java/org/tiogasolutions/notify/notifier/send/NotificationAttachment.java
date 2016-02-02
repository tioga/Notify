package org.tiogasolutions.notify.notifier.send;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * User: Harlan
 * Date: 1/24/2015
 * Time: 11:23 PM
 */
public final class NotificationAttachment {
  private final String name;
  private final String contentType;
  private final InputStream inputStream;

  public NotificationAttachment(String name, String contentType, InputStream inputStream) {
    this.name = (name != null) ? name : "no name";
    this.contentType = (contentType != null) ? contentType : "unknown";
    this.inputStream = inputStream;
  }

  public NotificationAttachment(String name, String contentType, byte[] content) {
    this(name, contentType, new ByteArrayInputStream(content));
  }

  public NotificationAttachment(String name, String contentType, String content) {
    this(name, contentType, (content != null ? content : "").getBytes(StandardCharsets.UTF_8));
  }

  public String getName() {
    return name;
  }

  public String getContentType() {
    return contentType;
  }

  public InputStream getInputStream() {
    return inputStream;
  }

  public void dispose() {
    try {
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NotificationAttachment that = (NotificationAttachment) o;

    if (!contentType.equals(that.contentType)) return false;
    if (inputStream != null ? !inputStream.equals(that.inputStream) : that.inputStream != null) return false;
    if (!name.equals(that.name)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + contentType.hashCode();
    result = 31 * result + (inputStream != null ? inputStream.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "NotificationRequestAttachment{" +
        "name='" + name + '\'' +
        ", contentType='" + contentType + '\'' +
        '}';
  }
}
