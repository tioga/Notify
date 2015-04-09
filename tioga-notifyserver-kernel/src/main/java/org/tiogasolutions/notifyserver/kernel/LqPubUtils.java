package org.tiogasolutions.notifyserver.kernel;

import org.tiogasolutions.notifyserver.kernel.request.LqAttachmentInfo;
import org.tiogasolutions.notifyserver.kernel.request.LqRequestEntity;
import org.tiogasolutions.notifyserver.notifier.request.LqExceptionInfo;
import org.tiogasolutions.notifyserver.pub.AttachmentInfo;
import org.tiogasolutions.notifyserver.pub.ExceptionInfo;
import org.tiogasolutions.notifyserver.pub.Request;
import org.tiogasolutions.notifyserver.pub.RequestStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to convert LqXxx objects to the corresponding pub objects. The two modules,
 * lq-notify and lq-pub do not have a common dependency tree and therefore cannot
 * effect these conversions themselves.
 */
public class LqPubUtils {

  public static ExceptionInfo toExceptionInfo(LqExceptionInfo that) {
    if (that == null) return null;

    return new ExceptionInfo(
      that.getExceptionType(),
      that.getMessage(),
      that.getStackTrace(),
      toExceptionInfo(that.getCause())
    );
  }

  public static List<AttachmentInfo> toAttachments(LqRequestEntity entity) {
    return entity.listAttachmentInfo().stream().map(LqPubUtils::toAttachmentInfo).collect(Collectors.toList());
  }

  public static Request toRequest(LqRequestEntity entity) {
    return new Request(
        entity.getRequestId(),
        entity.getRevision(),
        RequestStatus.valueOf(entity.getRequestStatus().name()),
        entity.getTopic(),
        entity.getSummary(),
        entity.getTrackingId(),
        entity.getCreatedAt(),
        entity.getTraitMap(),
        LqPubUtils.toExceptionInfo(entity.getExceptionInfo()),
        toAttachments(entity)
      );
  }

  public static AttachmentInfo toAttachmentInfo(LqAttachmentInfo lqAttachmentInfo) {
    return new AttachmentInfo(
      lqAttachmentInfo.getName(),
      lqAttachmentInfo.getContentType()
    );
  }
}
