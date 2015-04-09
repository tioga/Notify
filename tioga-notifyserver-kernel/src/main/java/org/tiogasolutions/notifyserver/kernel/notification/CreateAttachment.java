package org.tiogasolutions.notifyserver.kernel.notification;

import org.tiogasolutions.notifyserver.pub.NotificationRef;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: Harlan
 * Date: 1/24/2015
 * Time: 11:23 PM
 */
public final class CreateAttachment {
  private final NotificationRef notificationRef;
  private final String attachmentName;
  private final String contentType;
  private final InputStream inputStream;

  public CreateAttachment(NotificationRef notificationRef, String attachmentName, String contentType, InputStream inputStream) {
    this.notificationRef = notificationRef;
    this.attachmentName = (attachmentName != null) ? attachmentName : "no name";
    this.contentType = (contentType != null) ? contentType : "unknown";
    this.inputStream = inputStream;
  }

  public CreateAttachment(NotificationRef notificationRef, String attachmentName, String contentType, byte[] content) {
    this(notificationRef, attachmentName, contentType, new ByteArrayInputStream(content));
  }

  public NotificationRef getNotificationRef() {
    return notificationRef;
  }

  public String getAttachmentName() {
    return attachmentName;
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

}
