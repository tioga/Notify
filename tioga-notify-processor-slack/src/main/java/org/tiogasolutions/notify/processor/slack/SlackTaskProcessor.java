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

    public static final String DEFAULT_TEMPLATE_PATH = "classpath:/notify-processor-slack/default-slack-template.html";

    private final JsonTranslator jsonTranslator;
    private final ThymeleafMessageBuilder messageBuilder;


    private static final TaskProcessorType PROVIDER_TYPE = new TaskProcessorType("slack");
    private static final Logger log = LoggerFactory.getLogger(SlackTaskProcessor.class);

    private final Client client;

    @Autowired
    public SlackTaskProcessor(JsonTranslator jsonTranslator) {
        this.jsonTranslator = jsonTranslator;
        messageBuilder = new ThymeleafMessageBuilder();

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

            SlackMessage message = new SlackMessage().setText(messageText);

            // Set message attribute from valueMap.
            if (valueMap.containsKey("channel")) {
                message.setChannel(valueMap.get("channel"));
            }
            if (valueMap.containsKey("userName")) {
                message.setUsername(valueMap.get("userName"));
            }
            if (valueMap.containsKey("iconEmoji")) {
                message.setIconEmoji(valueMap.get("iconEmoji"));
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
                log.debug("Successfully sent Slack message: {}", notification.getSummary());
                return TaskResponse.complete("Ok");
            } else {
                String content = response.readEntity(String.class);
                String msg = String.format("Failure sending Slack message [%s]: %s", response.getStatus(), content);
                log.error(msg);
                log.error("Slack message JSON: " + json);
                return TaskResponse.fail(msg);
            }

        } catch (Throwable t) {
            log.error("Exception sending Slack message.", t);
            return TaskResponse.fail("Exception sending Slack message.", t);
        }
    }
}
