package org.tiogasolutions.notify.processor.slack;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.tiogasolutions.notify.pub.DomainProfile;
import org.tiogasolutions.notify.kernel.processor.ThymeleafMessageBuilder;
import org.tiogasolutions.notify.kernel.processor.ProcessorType;
import org.tiogasolutions.notify.kernel.processor.TaskProcessor;
import org.tiogasolutions.notify.pub.Notification;
import org.tiogasolutions.notify.pub.Task;
import org.tiogasolutions.notify.pub.TaskResponse;
import org.tiogasolutions.notify.pub.route.ArgValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SlackTaskProcessor implements TaskProcessor {

  public static final String DEFAULT_TEMPLATE_PATH = "classpath:/lq-processor-slack/default-slack-template.html";

  private final ThymeleafMessageBuilder messageBuilder;

  private static final ProcessorType PROVIDER_TYPE = new ProcessorType("slack");
  private static final Logger log = LoggerFactory.getLogger(SlackTaskProcessor.class);

  private final Client client;

  public SlackTaskProcessor() {

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
  public void init(BeanFactory beanFactory) {
  }

  @Override
  public ProcessorType getType() {
    return PROVIDER_TYPE;
  }

  @Override
  public TaskResponse processTask(DomainProfile domainProfile, Notification notification, Task task) {

    try {
      // Retrieve the url - mandatory
      ArgValueMap valueMap = task.getDestination().getArgValueMap();
      if (!valueMap.hasArg("slackUrl")) {
        throw ApiException.badRequest("Slack destination does not define a slackUrl argument");
      }
      String slackUrl = valueMap.asString("slackUrl");

      // Create the SlackMessage. In this case we are going to use the message builder
      // to run all our domain objects through a template processor.
      String templatePath = messageBuilder.getTemplatePath(valueMap, "templatePath", DEFAULT_TEMPLATE_PATH);
      String messageText = messageBuilder.createMessage(domainProfile, notification, task, templatePath);

      SlackMessage message = new SlackMessage().setText(messageText);

      // Set message attribute from valueMap.
      if (valueMap.hasArg("channel")) {
        message.setChannel(valueMap.asString("channel"));
      }
      if (valueMap.hasArg("userName")) {
        message.setUserName(valueMap.asString("userName"));
      }
      if (valueMap.hasArg("iconEmoji")) {
        message.setIconEmoji(valueMap.asString("iconEmoji"));
      }

      // Create entity
      String json = message.toJson();
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
