package org.tiogasolutions.notify.kernel.common;

import org.tiogasolutions.notify.kernel.request.NotificationRequestEntity;
import org.tiogasolutions.notify.notifier.request.NotificationExceptionInfo;
import org.tiogasolutions.notify.pub.request.NotificationRequest;
import org.tiogasolutions.notify.pub.request.NotificationRequestStatus;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;

/**
 * Used to convert notify objects to the corresponding pub objects. The two modules,
 * notify-notifier and notify-pub do not have a common dependency tree and therefore cannot
 * effect these conversions themselves.
 */
public class NotifyConversionUtils {

  public static ExceptionInfo toExceptionInfo(NotificationExceptionInfo that) {
    if (that == null) return null;

    return new ExceptionInfo(
      that.getExceptionType(),
      that.getMessage(),
      that.getStackTrace(),
      toExceptionInfo(that.getCause())
    );
  }

  public static NotificationRequest toRequest(NotificationRequestEntity entity) {
    return new NotificationRequest(
        entity.getRequestId(),
        entity.getRevision(),
        NotificationRequestStatus.valueOf(entity.getRequestStatus().name()),
        entity.getTopic(),
        entity.getSummary(),
        entity.getTrackingId(),
        entity.getCreatedAt(),
        entity.getTraitMap(),
        NotifyConversionUtils.toExceptionInfo(entity.getExceptionInfo()),
        entity.listAttachmentInfo()
      );
  }

}
