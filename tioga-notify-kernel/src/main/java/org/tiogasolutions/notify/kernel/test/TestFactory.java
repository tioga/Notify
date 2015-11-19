package org.tiogasolutions.notify.kernel.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.notify.kernel.config.CouchServers;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.notification.CreateNotification;
import org.tiogasolutions.notify.kernel.notification.NotificationEntity;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.kernel.task.CreateTask;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.common.ExceptionInfo;
import org.tiogasolutions.notify.pub.common.Link;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.domain.DomainStatus;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.notification.NotificationRef;
import org.tiogasolutions.notify.pub.route.DestinationDef;
import org.tiogasolutions.notify.pub.route.RouteCatalog;

import javax.xml.bind.DatatypeConverter;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Temporarily moved to main source so that we can reuse it in other modules.
 */
@Component
@Profile("test")
public class TestFactory {

  public static final String API_KEY = "9999";
  public static final String API_PASSWORD = "unittest";
  public static final String DOMAIN_NAME = "kernel";

  private final NotificationKernel notificationKernel;

  @Autowired
  public TestFactory(CouchServers couchServers, DomainKernel domainKernel, NotificationKernel notificationKernel) {
    try {
      this.notificationKernel = notificationKernel;

      // Delete any existing test databases.
      couchServers.deleteDomainDatabases(DOMAIN_NAME);

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

  public List<Link> newLinks() {
    return Collections.singletonList(new Link("example", "http://example.com"));
  }

  public CreateNotification newCreateNotificationWithException() {
    return new CreateNotification(
      "test-topic",
      "This is a test of the emergency broadcast system... this is only a test.",
      "tracking-id-123",
      ZonedDateTime.now(),
      newExceptionInfo(),
      newLinks(),
      BeanUtils.toMap("color:red", "sex:male", "test"));
  }

  public CreateNotification newCreateNotificationNoException() {
    return new CreateNotification(
      "test-topic",
      "This is a test of the emergency broadcast system... this is only a test.",
      "tracking-id-123",
      ZonedDateTime.now(),
      null,
      newLinks(),
      BeanUtils.toMap("color:red", "sex:male", "test"));
  }

  public ExceptionInfo newExceptionInfo() {
    return new ExceptionInfo(ApiException.forbidden("I'm sorry, I cannot let you do that.", new RuntimeException("I tripped when I was running with scissors")));
  }

  public String toHttpAuth(String username, String password) {
    byte[] value = (username + ":" + password).getBytes();
    return "Basic " + DatatypeConverter.printBase64Binary(value);
  }
}
