package org.tiogasolutions.notify.pub.notification;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User: Harlan
 * Date: 1/31/2015
 * Time: 7:15 PM
 */
public class NotificationRef {
    private final String domainName;
    private final String notificationId;
    private final String revision;

    public NotificationRef(@JsonProperty("domainName") String domainName,
                           @JsonProperty("notificationId") String notificationId,
                           @JsonProperty("revision") String revision) {

        this.domainName = domainName;
        this.notificationId = notificationId;
        this.revision = revision;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getRevision() {
        return revision;
    }

    @Override
    public String toString() {
        return "NotificationRef{" +
                "domainName='" + domainName + '\'' +
                ", notificationId='" + notificationId + '\'' +
                ", revision='" + revision + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationRef that = (NotificationRef) o;

        if (!domainName.equals(that.domainName)) return false;
        if (!notificationId.equals(that.notificationId)) return false;
        if (!revision.equals(that.revision)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domainName.hashCode();
        result = 31 * result + notificationId.hashCode();
        result = 31 * result + revision.hashCode();
        return result;
    }
}
