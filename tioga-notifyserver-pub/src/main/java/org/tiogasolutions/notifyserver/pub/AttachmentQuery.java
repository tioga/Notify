package org.tiogasolutions.notifyserver.pub;

/**
 * User: Harlan
 * Date: 2/8/2015
 * Time: 12:28 AM
 */
public class AttachmentQuery {
    private String notificationId;
    private String attachmentName;
    private int limit = 100;

    public AttachmentQuery forNotification(Notification notification) {
        this.notificationId = notification.getNotificationId();
        return this;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public AttachmentQuery setNotificationId(String notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public AttachmentQuery setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public AttachmentQuery setLimit(int limit) {
        this.limit = limit;
        return this;
    }
}
