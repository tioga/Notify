package org.lqnotify.processor.push;

import com.cosmicpush.gateway.CosmicPushGateway;
import com.cosmicpush.pub.common.Push;
import com.cosmicpush.pub.push.EmailPush;
import com.cosmicpush.pub.push.TwilioSmsPush;
import com.cosmicpush.pub.push.XmppPush;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.UnsupportedMethodException;
import org.lqnotify.kernel.processor.HtmlMessage;
import org.lqnotify.kernel.processor.ThymeleafMessageBuilder;
import org.lqnotify.kernel.processor.ProcessorType;
import org.lqnotify.kernel.processor.TaskProcessor;
import org.lqnotify.pub.*;
import org.lqnotify.pub.route.ArgValue;
import org.lqnotify.pub.route.ArgValueMap;
import org.lqnotify.pub.route.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.*;

public class PushTaskProcessor implements TaskProcessor {

  private static final Logger log = LoggerFactory.getLogger(PushTaskProcessor.class);
  private static final ProcessorType PROCESSOR_TYPE = new ProcessorType("push");

  private final ThymeleafMessageBuilder messageBuilder;
  /*package*/ CosmicPushGateway gateway;
  /*package*/ private PushConfig pushConfig;

  public PushTaskProcessor() {
    this.messageBuilder = new ThymeleafMessageBuilder();
  }

  @Override
  public void init(BeanFactory beanFactory) {
    this.pushConfig = beanFactory.getBean(PushConfig.class);
    this.gateway = beanFactory.getBean(CosmicPushGateway.class);
  }

  @Override
  public ProcessorType getType() {
    return PROCESSOR_TYPE;
  }

  @Override
  public boolean isReady() {
    try {
      gateway.ping();
      return true;

    } catch (Exception e) {
      log.warn("The push-server is not responding to a ping.");
      return false;
    }
  }

  @Override
  public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {
    if (gateway == null) {
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
    pushList.forEach(gateway::send);

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
