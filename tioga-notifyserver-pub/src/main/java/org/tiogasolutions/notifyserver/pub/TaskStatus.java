package org.tiogasolutions.notifyserver.pub;

public enum TaskStatus {

  PENDING,
  SENDING,
  COMPLETED,
  FAILED;

  private TaskStatus() {
  }

  public boolean isPending() {
    return this == PENDING;
  }

  public boolean isSending() {
    return this == SENDING;
  }

  public boolean isCompleted() {
    return this == COMPLETED;
  }

  public boolean isFailed() {
    return this == FAILED;
  }
}
