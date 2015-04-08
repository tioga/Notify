package org.lqnotify.kernel.task;

import org.lqnotify.pub.route.Destination;
import org.lqnotify.pub.NotificationRef;

public class CreateTask {
  // While not following the exact same pattern as CreateNotification and CreateAttachment,
  // the use of this class is still valuable in that it follows the action pattern which
  // further promotes encapsulating the validation of this action.
  // TODO - add validation to this class.

  private final String notificationId;
  private final Destination destination;

  private CreateTask(String notificationId, Destination destination) {
    this.notificationId = notificationId;
    this.destination = destination;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public Destination getDestination() {
    return destination;
  }

  public static CreateTask create(NotificationRef notification, Destination destination) {
    return new CreateTask(notification.getNotificationId(), destination);
  }
}
