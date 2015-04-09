package org.tiogasolutions.notifyserver.pub;

public enum NotificationStatus {

  received, assigned, completed, failed;

  private NotificationStatus() {
  }

  public boolean isReceived() {
    return this == received;
  }

  public boolean isAssigned() {
    return this == assigned;
  }

  public boolean isCompleted() {
    return this == completed;
  }

  public boolean isFailed() {
    return this == failed;
  }
}
