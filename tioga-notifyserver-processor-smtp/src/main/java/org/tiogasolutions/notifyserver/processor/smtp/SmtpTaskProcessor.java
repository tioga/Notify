package org.tiogasolutions.notifyserver.processor.smtp;

import org.tiogasolutions.dev.domain.comm.AuthenticationMethod;
import org.tiogasolutions.notifyserver.kernel.processor.HtmlMessage;
import org.tiogasolutions.notifyserver.kernel.processor.ThymeleafMessageBuilder;
import org.tiogasolutions.notifyserver.kernel.processor.ProcessorType;
import org.tiogasolutions.notifyserver.kernel.processor.TaskProcessor;
import org.tiogasolutions.notifyserver.pub.route.ArgValueMap;
import org.springframework.beans.factory.BeanFactory;
import org.tiogasolutions.notifyserver.pub.DomainProfile;
import org.tiogasolutions.notifyserver.pub.Notification;
import org.tiogasolutions.notifyserver.pub.Task;
import org.tiogasolutions.notifyserver.pub.TaskResponse;

import java.util.Arrays;

/**
 * Created by jacobp on 3/18/2015.
 */
public class SmtpTaskProcessor implements TaskProcessor {

  private static final ProcessorType PROCESSOR_TYPE = new ProcessorType("smtp");

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

    EmailMessage message = new EmailMessage(host, port, Arrays.asList(recipient));
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
  public ProcessorType getType() {
    return PROCESSOR_TYPE;
  }

  @Override
  public void init(BeanFactory beanFactory) {

  }
}
