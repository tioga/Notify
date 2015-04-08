package org.lqnotify.kernel;

import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.lqnotify.kernel.domain.DomainKernel;
import org.lqnotify.kernel.execution.ExecutionContext;
import org.lqnotify.kernel.notification.CreateNotification;
import org.lqnotify.kernel.notification.NotificationEntity;
import org.lqnotify.kernel.notification.NotificationKernel;
import org.lqnotify.kernel.task.CreateTask;
import org.lqnotify.kernel.task.TaskEntity;
import org.lqnotify.pub.*;
import org.lqnotify.pub.route.DestinationDef;
import org.lqnotify.pub.route.RouteCatalog;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.DatatypeConverter;
import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * Temporarily moved to main source so that we can reuse it in other modules.
 */
@Named
@Profile("test")
public class TestFactory {

  public static final String API_KEY = "9999";
  public static final String API_PASSWORD = "unittest";
  public static final String DOMAIN_NAME = "kernel-test";

  private final NotificationKernel notificationKernel;

  @Inject
  public TestFactory(DomainKernel domainKernel, NotificationKernel notificationKernel) {
    try {
      this.notificationKernel = notificationKernel;

      // Create a test domain.
      domainKernel.recreateDomain(DOMAIN_NAME, API_KEY, API_PASSWORD);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public TaskEntity newEmailTaskEntity(Notification notification) {
    DestinationDef destinationDef = new DestinationDef("local", "push")
        .addArg("type", "emailMsg")
        .addArg("recipient", "test@jacobparr.com");
    CreateTask create = CreateTask.create(notification.toNotificationRef(), destinationDef.toDestination());
    return notificationKernel.createTask(create, notification);
  }

  public NotificationRef newNotificationRef(CreateNotification create) {
    return notificationKernel.createNotification(create);
  }

  public DomainProfile newDomainProfile() {
    return new DomainProfile(
      "777", "r-3", "TestDomain", DomainStatus.ACTIVE,
      "some-api-key", "some-api-passowrd",
      "notification-db", "request-db",
      new RouteCatalog(Collections.emptyList(), Collections.emptyList())
    );
  }

  public Notification newNotification(CreateNotification create) {
    NotificationRef notificationRef = notificationKernel.createNotification(create);
    return notificationKernel.findNotificationById(notificationRef.getNotificationId());
  }

  public NotificationEntity newNotification(ExecutionContext execContext, CreateNotification create) {
    return NotificationEntity.newEntity(execContext.getDomainName(), create);
  }

  public CreateNotification newCreateNotificationWithException() {
    return new CreateNotification(
      "test-topic",
      "This is a test of the emergency broadcast system... this is only a test.",
      "tracking-id-123",
      ZonedDateTime.now(),
      newExceptionInfo(),
      BeanUtils.toMap("color:red", "sex:male", "test"));
  }

  public CreateNotification newCreateNotificationNoException() {
    return new CreateNotification(
      "test-topic",
      "This is a test of the emergency broadcast system... this is only a test.",
      "tracking-id-123",
      ZonedDateTime.now(),
      null,
      BeanUtils.toMap("color:red", "sex:male", "test"));
  }

  public ExceptionInfo newExceptionInfo() {
    return new ExceptionInfo(ApiException.forbidden("I'm sorry, I cannot let you do that.", new RuntimeException("I tripped when I was running with scissors")));
  }

  public String toHttpAuth(String username, String password) {
    byte[] value = (username + ":" + password).getBytes();
    return "Basic " + DatatypeConverter.printBase64Binary(value);
  }
//  jacobp:Testing123
//  "Basic OTk5OTp1bml0dGVzdA==";
//  public static final String API_ADMIN_AUTH = "Basic amFjb2JwOlRlc3RpbmcxMjM=";
//  public static final String API_BAD_AUTH = "Basic YmFkLWd1eTpPcGVuIFNlc2FtZQ==";
}
