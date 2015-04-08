package org.lqnotify.pub;

/**
 * User: Harlan
 * Date: 2/8/2015
 * Time: 12:28 AM
 */
public class NotificationQuery {

  private String notificationId;
  private String topic;
  private String trackingId;
  private String traitKey;
  private String traitValue;
  private String summary;
  private int offset = 0;
  private int limit = 100;

  public NotificationQuery() {
  }

  public String getNotificationId() {
    return notificationId;
  }

  public NotificationQuery setNotificationId(String notificationId) {
    this.notificationId = notificationId;
    return this;
  }

  public String getTopic() {
    return topic;
}

  public NotificationQuery setTopic(String topic) {
      this.topic = topic;
      return this;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public NotificationQuery setTrackingId(String trackingId) {
    this.trackingId = trackingId;
    return this;
  }

  public String getTraitKey() {
    return traitKey;
  }

  public NotificationQuery setTraitKey(String traitKey) {
    this.traitKey = traitKey;
    return this;
  }

  public String getTraitValue() {
    return traitValue;
  }

  public NotificationQuery setTraitValue(String traitValue) {
    this.traitValue = traitValue;
    return this;
  }

  public String getSummary() {
    return summary;
  }

  public NotificationQuery setSummary(String summary) {
    this.summary = summary;
    return this;
  }

  public int getLimit() {
      return limit;
  }

  public NotificationQuery setLimit(int limit) {
      this.limit = limit;
      return this;
  }

  public int getOffset() {
    return offset;
  }

  public NotificationQuery setOffset(int offset) {
    this.offset = offset;
    return this;
  }

}
