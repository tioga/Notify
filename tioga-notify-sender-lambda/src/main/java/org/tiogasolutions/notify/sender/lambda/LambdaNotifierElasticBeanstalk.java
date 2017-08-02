package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.tiogasolutions.notify.notifier.builder.NotificationBuilder;

import java.util.Map;

public class LambdaNotifierElasticBeanstalk extends LambdaNotifier {

    @Override
    protected String buildTraits(Map<String, String> traits, SNSEvent.SNSRecord record) {
        super.buildTraits(traits, record);

        String message = traits.get("Message");
        if (message != null) {
            traits.remove("Message");
            return trim(message);
        }

        message = record.getSNS().getMessage();
        if (message != null) return trim(message);

        return record.getSNS().getSubject();
    }

    protected String trim(String message) {
        if (message == null) return null;

        message = message.replace("\r", "");

        int pos = message.indexOf("\n");
        if (pos < 0) return message;

        return message.substring(0, pos) + "...";
    }

    @Override
    protected void decorateNotification(NotificationBuilder builder, SNSEvent.SNSRecord record) {
        String topic = System.getProperty("NOTIFIER_TOPIC");
        if (topic == null) topic = System.getenv("NOTIFIER_TOPIC");
        if (topic == null) topic = "AWS Elastic Beanstalk";
        builder.topic(topic);

        String summary = builder.getSummary();
        if (summary != null && summary.startsWith("Environment health has transitioned from ")) {
            if (summary.contains(" to Severe.")) {
                builder.trait("action", "error");
            } else if (summary.contains(" to Info.")) {
                builder.trait("action", "warning");
            } else {
                builder.trait("action", "unknown");
            }
        } else {
            builder.trait("action", "unknown");
        }
    }
}
