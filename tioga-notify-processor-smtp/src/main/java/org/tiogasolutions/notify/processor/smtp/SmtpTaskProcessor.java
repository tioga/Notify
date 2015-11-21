package org.tiogasolutions.notify.processor.smtp;

import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.domain.comm.AuthenticationMethod;
import org.tiogasolutions.notify.kernel.message.HtmlMessage;
import org.tiogasolutions.notify.kernel.message.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.kernel.task.TaskProcessor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorType;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.ArgValueMap;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskResponse;

import static java.util.Collections.singletonList;

@Component
public class SmtpTaskProcessor implements TaskProcessor {

  private static final TaskProcessorType PROCESSOR_TYPE = new TaskProcessorType("smtp");

  private final ThymeleafMessageBuilder messageBuilder;

  public SmtpTaskProcessor() {
    this.messageBuilder = new ThymeleafMessageBuilder();
  }

  @Override
  public boolean isReady() {
    // TODO - validate that the SMTP server is available.
    return true;
  }

  @Override
  public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {

    ArgValueMap argMap = task.getDestination().getArgValueMap();
    String templatePath = messageBuilder.getEmailTemplatePath(argMap, "templatePath");
    HtmlMessage htmlMessage = messageBuilder.createHtmlMessage(domainProfile, notification, task, templatePath);

    EmailMessage emailMessage = createEmailMessage(argMap);
    emailMessage.send(htmlMessage.getSubject(), null, htmlMessage.getBody());

    return TaskResponse.complete("Email sent");
  }


  protected EmailMessage createEmailMessage(ArgValueMap argMap) {

    SmtpAuthType authType = argMap.asEnum(SmtpAuthType.class, "smtpAuthType");
    String host = argMap.asString("smtpHost");
    String port = argMap.asString("smtpPort");
    String username = argMap.asString("smtpUsername");
    String password = argMap.asString("smtpPassword");
    String from = argMap.asString("smtpFrom");
    String recipient = argMap.asString("smtpRecipients");

    EmailMessage message = new EmailMessage(host, port, singletonList(recipient));
    message.setFrom(from);

    if (authType.isTls()) {
      message.setAuthentication(AuthenticationMethod.TLS, username, password);
    } else if (authType.isSsl()) {
      message.setAuthentication(AuthenticationMethod.SSL, username, password);
    } else {
      message.setAuthentication(AuthenticationMethod.NONE, username, password);
    }

    return message;
  }

  @Override
  public TaskProcessorType getType() {
    return PROCESSOR_TYPE;
  }
}
