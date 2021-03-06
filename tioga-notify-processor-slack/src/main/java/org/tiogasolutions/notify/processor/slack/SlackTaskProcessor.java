package org.tiogasolutions.notify.processor.slack;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.notify.kernel.message.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.kernel.task.TaskProcessor;
import org.tiogasolutions.notify.kernel.task.TaskProcessorType;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
public class SlackTaskProcessor implements TaskProcessor {

    public static final String DEFAULT_TEMPLATE_PATH = "classpath:/tioga-notify-processor-slack/default-slack-template.html";
    private static final TaskProcessorType PROVIDER_TYPE = new TaskProcessorType("slack");
    private static final Logger log = LoggerFactory.getLogger(SlackTaskProcessor.class);
    private final JsonTranslator jsonTranslator;
    private final ThymeleafMessageBuilder messageBuilder;
    private final Client client;
    private final Notifier notifier;

    @Autowired
    public SlackTaskProcessor(JsonTranslator jsonTranslator, Notifier notifier) {
        this.notifier = notifier;
        this.jsonTranslator = jsonTranslator;
        this.messageBuilder = new ThymeleafMessageBuilder();

        // Build the client
        Configuration httpClientConfig = new ClientConfig()
                .register(MultiPartFeature.class);

        ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(httpClientConfig);
        client = clientBuilder.build();
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public TaskProcessorType getType() {
        return PROVIDER_TYPE;
    }

    @Override
    public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {

        log.info("Started processing task {} for notification {}.", task.getTaskId(), notification.getNotificationId());

        try {
            // Retrieve the url - mandatory
            Map<String, String> valueMap = task.getDestination().getArguments();
            if (!valueMap.containsKey("slackUrl")) {
                throw ApiException.badRequest("Slack destination does not define a slackUrl argument");
            }
            String slackUrl = valueMap.get("slackUrl");

            // Create the SlackMessage. In this case we are going to use the message builder
            // to run all our domain objects through a template processor.
            String templatePath = messageBuilder.getTemplatePath(valueMap, "templatePath", DEFAULT_TEMPLATE_PATH);
            String messageText = messageBuilder.createMessage(domainProfile, notification, task, templatePath);
            messageText = messageText.replace("\n<br />\n\n\n", "\n");
            messageText = messageText.replace("\n<br />\n\n", "\n");
            messageText = messageText.replace("\n<br />\n", "\n");

            // If the message has a URL in it, SLACK will automatically attempt to fetch it.
            // In the case of 404, this means that posting that a URL was not found will result
            // in an endless cycle of posting, fetching, posting, fetching, etc...
            // char vtab = 11;
            // String vtab = ":"; // not a colon, but looks like one :-)
            // messageText = messageText.replace("HTTP://", "HTTP" + vtab + "//");
            // messageText = messageText.replace("http://", "http" + vtab + "//");
            // messageText = messageText.replace("HTTPS://", "HTTPS" + vtab + "//");
            // messageText = messageText.replace("https://", "https" + vtab + "//");

            // But there are good links, namely the link to our notification.
            // We can identify them by via slack's special mark up for links.
            // messageText = messageText.replace("<HTTP" + vtab + "//", "<HTTP://");
            // messageText = messageText.replace("<http" + vtab + "//", "<http://");
            // messageText = messageText.replace("<HTTPS" + vtab + "//", "<HTTPS://");
            // messageText = messageText.replace("<https" + vtab + "//", "<https://");

            SlackMessage message = new SlackMessage().setText(messageText);

            // Set message attribute from valueMap.
            if (valueMap.containsKey("channel")) {
                message.setChannel(valueMap.get("channel"));
            }
            if (valueMap.containsKey("username")) {
                String id = notification.getNotificationId();
                String username = valueMap.get("username");
                username = username.replace("{{id}}", id.substring(0, id.indexOf("-")));
                message.setUsername(username);
            }
            if (valueMap.containsKey("iconEmoji")) {
                message.setIconEmoji(valueMap.get("iconEmoji"));
            }
            if (valueMap.containsKey("iconUrl")) {
                message.setIconUrl(valueMap.get("iconUrl"));
            }

            // Create entity
            String json = jsonTranslator.toJson(message);
            Entity entity = Entity.entity(json, MediaType.APPLICATION_JSON_TYPE);

            // Post the message
            Response response = client
                    .target(slackUrl)
                    .request()
                    .post(entity);

            if (response.getStatus() == 200 || response.getStatus() == 201) {
                log.info("Successfully sent Slack message: {}", notification.getSummary());
                return TaskResponse.complete("Ok");

            } else {
                String content = response.readEntity(String.class);
                String msg = String.format("Failure sending Slack message [%s]: %s", response.getStatus(), content);
                notify(notification, ApiException.fromCode(response.getStatus()), msg, json);

                return TaskResponse.fail(msg);
            }

        } catch (Exception e) {
            notify(notification, e, "Exception sending Slack message.", null);
            return TaskResponse.fail("Exception sending Slack message.", e);
        }
    }

    private void notify(Notification notification, Exception e, String msg, String json) {
        try {
            if (notification != null && notification.isInternalException()) log.error("SUPPRESSED: "+msg, e);
            else notifier.begin().summary(msg).exception(e).trait("json", json).send().get();
        } catch (Exception ex) {
            log.error("Exception sending notification", ex);
        }
    }
}
