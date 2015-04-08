package org.lqnotify.pub;

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
