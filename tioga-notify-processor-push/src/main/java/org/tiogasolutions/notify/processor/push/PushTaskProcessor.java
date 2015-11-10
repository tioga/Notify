package org.tiogasolutions.notify.processor.push;

import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.exceptions.ApiUnauthorizedException;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.push.client.LivePushServerClient;
import org.tiogasolutions.push.client.PushServerClient;
import org.tiogasolutions.push.pub.common.Push;
import org.tiogasolutions.push.pub.EmailPush;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.UnsupportedMethodException;
import org.tiogasolutions.notify.kernel.message.HtmlMessage;
import org.tiogasolutions.notify.kernel.message.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.kernel.task.TaskProcessorType;
import org.tiogasolutions.notify.kernel.task.TaskProcessor;
import org.tiogasolutions.notify.pub.route.ArgValue;
import org.tiogasolutions.notify.pub.route.ArgValueMap;
import org.tiogasolutions.notify.pub.route.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.*;

public class PushTaskProcessor implements TaskProcessor {

  private static final Logger log = LoggerFactory.getLogger(PushTaskProcessor.class);
  private static final TaskProcessorType PROCESSOR_TYPE = new TaskProcessorType("push");

  private final ThymeleafMessageBuilder messageBuilder;
  /*package*/ PushServerClient client;
  /*package*/ private PushConfig pushConfig;

  public PushTaskProcessor() {
    this.messageBuilder = new ThymeleafMessageBuilder();
  }

  @Override
  public void init(BeanFactory beanFactory) {
    this.pushConfig = beanFactory.getBean(PushConfig.class);
    this.client = beanFactory.getBean(PushServerClient.class);
  }

  @Override
  public TaskProcessorType getType() {
    return PROCESSOR_TYPE;
  }

  @Override
  public boolean isReady() {
    try {
      client.ping();
      return true;

    } catch (ApiUnauthorizedException e) {

      String msg = "Credentials for the push-server are invalid (domain = \"";
      if (client instanceof LivePushServerClient) {
        msg += ((LivePushServerClient)client).getClient().getUsername();
        msg += "\")";
      }

      log.error(msg);

    } catch (ApiNotFoundException e) {
      String msg = "The push-server client is improperly configured";
      if (client instanceof LivePushServerClient) {
        msg += ": " + ((LivePushServerClient)client).getClient().getApiUrl();
      }
      log.error(msg);

    } catch (Exception e) {
      log.warn("The push-server is not responding to a ping.", e);
    }
    return false;
  }

  @Override
  public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {
    if (client == null) {
      return TaskResponse.retry("Push gateway was not yet set.");
    }

    log.debug("Processing task: " + task);

    Destination destination = task.getDestination();
    ArgValueMap argMap = destination.getArgValueMap();
    if (!argMap.hasArg("type")) {
      throw ApiException.badRequest("Task given to push processor which does not have a type.");
    }

    List<String> recipients = readRecipients(argMap);
    if (recipients.isEmpty()) {
      throw ApiException.badRequest("Task given to push processor which does not have any recipients.");
    }

    List<Push> pushList;
    PushDestinationType destinationType = PushDestinationType.valueOf(argMap.asString("type"));
    if (destinationType.isSmsMsg()) {
      pushList = toSmsPush(notification, recipients);

    } else if (destinationType.isJabberMsg()) {
      pushList = toJabber(notification, recipients);

    } else if (destinationType.isEmailMsg()) {
      pushList = toEmailPush(domainProfile, notification, task, argMap, recipients);

    } else if (destinationType.isPhoneCall()) {
      pushList = toPhoneCallPush();

    } else {
      String msg = format("The task type \"%s\" is not supported.", destinationType);
      throw new UnsupportedOperationException(msg);
    }

    // The last thing we need to do is push the Push.
    pushList.forEach(client::send);

    return TaskResponse.complete("Ok");
  }

  /**
   * HACK - this is overkill but I wanted things to work, could be cleaned up for sure - HN
   * @param argMap -
   * @return List<String>
   */
  private List<String> readRecipients(ArgValueMap argMap) {
    List<String> recipients = new ArrayList<>();
    if (argMap.hasArg("recipient")) {
      ArgValue argValue = argMap.asValue("recipient");
      if (argValue.getArgType() == ArgValue.ArgType.STRING) {
        recipients.add(argValue.asString());
      }
      if (argValue.getArgType() == ArgValue.ArgType.LIST) {
        recipients.addAll(argValue.asList().stream().map(ArgValue::asString).collect(Collectors.toList()));
      }
    }
    if (argMap.hasArg("recipients")) {
      ArgValue argValue = argMap.asValue("recipients");
      if (argValue.getArgType() == ArgValue.ArgType.STRING) {
        recipients.add(argValue.asString());
      }
      if (argValue.getArgType() == ArgValue.ArgType.LIST) {
        recipients.addAll(argValue.asList().stream().map(ArgValue::asString).collect(Collectors.toList()));
      }
    }
    return recipients;
  }

  private List<Push> toJabber(Notification notification, List<String> recipients) {
    return recipients.stream()
        .map(recipient -> XmppPush.newPush(recipient, notification.getSummary(), null))
        .collect(Collectors.toList());
  }

  private List<Push> toSmsPush(Notification notification, List<String> recipients) {
    return recipients.stream()
        .map(recipient -> TwilioSmsPush.newPush(pushConfig.getSmsFromNumber(), recipient, notification.getSummary(), null))
        .collect(Collectors.toList());
  }

  private List<Push> toPhoneCallPush() {
    throw new UnsupportedMethodException();
    // return TwilioSmsPush.newPush(pushConfig.getPhoneFromNumber(), task.getRecipient(), notification.getSummary(), null);
  }

  private List<Push> toEmailPush(DomainProfile domainProfile, Notification notification, Task task, ArgValueMap argMap, List<String> recipients) {
    String templatePath = messageBuilder.getEmailTemplatePath(argMap, "templatePath");
    HtmlMessage message = messageBuilder.createHtmlMessage(domainProfile, notification, task, templatePath);
    return recipients.stream()
        .map(recipient -> EmailPush.newPush(recipient, pushConfig.getEmailFromAddress(), message.getSubject(), message.getBody(), null))
        .collect(Collectors.toList());
  }
}
