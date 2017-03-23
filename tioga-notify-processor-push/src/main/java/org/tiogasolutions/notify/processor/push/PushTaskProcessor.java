package org.tiogasolutions.notify.processor.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.exceptions.UnsupportedMethodException;
import org.tiogasolutions.notify.kernel.message.HtmlMessage;
import org.tiogasolutions.notify.kernel.message.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.kernel.task.TaskProcessor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorType;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskResponse;
import org.tiogasolutions.push.client.PushServerClient;
import org.tiogasolutions.push.pub.SesEmailPush;
import org.tiogasolutions.push.pub.SmtpEmailPush;
import org.tiogasolutions.push.pub.TwilioSmsPush;
import org.tiogasolutions.push.pub.XmppPush;
import org.tiogasolutions.push.pub.common.Push;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.tiogasolutions.dev.common.StringUtils.isBlank;
import static org.tiogasolutions.dev.common.StringUtils.tokenize;

@Component
public class PushTaskProcessor implements TaskProcessor {

    private static final Logger log = LoggerFactory.getLogger(PushTaskProcessor.class);
    private static final TaskProcessorType PROCESSOR_TYPE = new TaskProcessorType("push");

    private final PushClientFactory factory;
    private final ThymeleafMessageBuilder messageBuilder;

    @Autowired
    public PushTaskProcessor(PushClientFactory factory) {
        this.factory = factory;
        this.messageBuilder = new ThymeleafMessageBuilder();
    }

    @Override
    public TaskProcessorType getType() {
        return PROCESSOR_TYPE;
    }

    @Override
    public boolean isReady() {
        return true;

//    try {
//      client.ping();
//      return true;
//
//    } catch (ApiUnauthorizedException e) {
//
//      String msg = "Credentials for the push-server are invalid (domain = \"";
//      if (client instanceof LivePushServerClient) {
//        msg += ((LivePushServerClient)client).getClient().getUsername();
//        msg += "\")";
//      }
//
//      log.error(msg);
//
//    } catch (ApiNotFoundException e) {
//      String msg = "The push-server client is improperly configured";
//      if (client instanceof LivePushServerClient) {
//        msg += ": " + ((LivePushServerClient)client).getClient().getApiUrl();
//      }
//      log.error(msg);
//
//    } catch (Exception e) {
//      log.warn("The push-server is not responding to a ping.", e);
//    }
//    return false;
    }

    @Override
    public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {
        log.debug("Processing task: " + task);

        Destination destination = task.getDestination();
        Map<String, String> argMap = destination.getArguments();

        String url = argMap.get("url");
        if (isBlank(url)) {
            throw ApiException.badRequest("Task given to push processor which does specify the \"url\" of the push server.");
        }
        PushServerClient client = factory.createPushServerClient(url);

        String type = argMap.get("type");
        if (isBlank(type)) {
            throw ApiException.badRequest("Task given to push processor which does specify the \"type\" of push.");
        }

        List<String> recipients = readRecipients(argMap);
        if (recipients.isEmpty()) {
            throw ApiException.badRequest("Task given to push processor which does not have any \"recipients\".");
        }

        String from = argMap.get("from");
        if (isBlank(from)) {
            throw ApiException.badRequest("Task given to push processor which does not have a \"from\" indicator.");
        }

        List<Push> pushList;
        PushDestinationType destinationType = PushDestinationType.valueOf(type);
        if (destinationType.isSmsMsg()) {
            pushList = toSmsPush(notification, from, recipients);

        } else if (destinationType.isJabberMsg()) {
            pushList = toJabber(notification, recipients);

        } else if (destinationType.isSesEmailMsg()) {
            pushList = toSesEmailPush(domainProfile, notification, task, argMap, from, recipients);

        } else if (destinationType.isSmtpEmailMsg()) {
            pushList = toSmtpEmailPush(domainProfile, notification, task, argMap, from, recipients);

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
     *
     * @param argMap -
     * @return List<String>
     */
    private List<String> readRecipients(Map<String, String> argMap) {
        List<String> recipients = new ArrayList<>();
        if (argMap.containsKey("recipients")) {
            String value = argMap.get("recipients");
            if (value.contains(",")) {
                for (String recipient : tokenize(value, ',')) {
                    recipients.add(recipient.trim());
                }
            } else if (value.contains(";")) {
                for (String recipient : tokenize(value, ';')) {
                    recipients.add(recipient.trim());
                }
            } else {
                recipients.add(value);
            }
        }
        return recipients;
    }

    private List<Push> toJabber(Notification notification, List<String> recipients) {
        return recipients.stream()
                .map(recipient -> XmppPush.newPush(recipient, notification.getSummary(), null))
                .collect(Collectors.toList());
    }

    private List<Push> toSmsPush(Notification notification, String from, List<String> recipients) {
        return recipients.stream()
                .map(recipient -> TwilioSmsPush.newPush(from, recipient, notification.getSummary(), null))
                .collect(Collectors.toList());
    }

    private List<Push> toPhoneCallPush() {
        throw new UnsupportedMethodException();
        // return TwilioSmsPush.newPush(pushConfig.getPhoneFromNumber(), task.getRecipient(), notification.getSummary(), null);
    }

    private List<Push> toSesEmailPush(DomainProfile domainProfile, Notification notification, Task task, Map<String,String> argMap, String from, List<String> recipients) {
        String templatePath = messageBuilder.getEmailTemplatePath(argMap, "templatePath");
        HtmlMessage message = messageBuilder.createHtmlMessage(domainProfile, notification, task, templatePath);
        return recipients.stream()
                .map(recipient -> SesEmailPush.newPush(recipient, from, message.getSubject(), message.getBody(), null))
                .collect(Collectors.toList());
    }

    private List<Push> toSmtpEmailPush(DomainProfile domainProfile, Notification notification, Task task, Map<String,String> argMap, String from, List<String> recipients) {
        String templatePath = messageBuilder.getEmailTemplatePath(argMap, "templatePath");
        HtmlMessage message = messageBuilder.createHtmlMessage(domainProfile, notification, task, templatePath);
        return recipients.stream()
                .map(recipient -> SmtpEmailPush.newPush(recipient, from, message.getSubject(), message.getBody(), null))
                .collect(Collectors.toList());
    }
}
