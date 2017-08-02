package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.builder.NotificationBuilder;
import org.tiogasolutions.notify.notifier.send.NotificationSender;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponseType;
import org.tiogasolutions.notify.sender.http.HttpNotificationSender;
import org.tiogasolutions.notify.sender.http.HttpNotificationSenderConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class LambdaNotifier implements RequestHandler<SNSEvent, Object> {

    public LambdaNotifier() {
    }

    private void log(Context context, String msg) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.sql.Timestamp(System.currentTimeMillis()));
        context.getLogger().log("["+timestamp+"] " + msg.trim() + "\n");
    }

    public Object handleRequest(SNSEvent request, Context context){
        HttpNotificationSenderConfig config = new HttpNotificationSenderConfig();

        String url = System.getProperty("NOTIFIER_URL");
        if (url == null) url = System.getenv("NOTIFIER_URL");
        config.setUrl(url);

        String username = System.getProperty("NOTIFIER_USERNAME");
        if (username == null) username = System.getenv("NOTIFIER_USERNAME");
        config.setUserName(username);

        String password = System.getProperty("NOTIFIER_PASSWORD");
        if (password == null) password = System.getenv("NOTIFIER_PASSWORD");
        config.setPassword(password);

        NotificationSender sender = new HttpNotificationSender(config);
        Notifier notifier = new Notifier(sender);

        log(context, "Invocation started.");

        SNSEvent.SNSRecord record = request.getRecords().get(0);
        return handleRequest(notifier, record, context);
    }

    protected Object handleRequest(Notifier notifier, SNSEvent.SNSRecord record, Context context) {

        Map<String,String> traits = new LinkedHashMap<>();

        String subject = buildTraits(traits, record);

        NotificationBuilder builder = notifier.begin().summary(subject);
        log(context, "Subject: " + subject);

        for (Map.Entry<String, String> entry : traits.entrySet()) {
            builder.trait(entry.getKey(), entry.getValue());
            log(context, String.format("  %s: %s", entry.getKey(), entry.getValue()));
        }

        decorateNotification(builder, record);

        try {
            SendNotificationResponse response = builder.send().get();

            if (response.getResponseType() == SendNotificationResponseType.FAILURE) {
                StringWriter writer = new StringWriter();
                response.getThrowable().printStackTrace(new PrintWriter(writer));
                return writer.toString();

            } else {
                return String.format("%s: %s", response.getResponseType(), response.getRequest().getSummary());
            }
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            return writer.toString();

        } finally {
            log(context, "Invocation completed.");
        }
    }

    protected void decorateNotification(NotificationBuilder builder, SNSEvent.SNSRecord record) {
        String topic = System.getProperty("NOTIFIER_TOPIC");
        if (topic == null) topic = System.getenv("NOTIFIER_TOPIC");
        if (topic == null) topic = "AWS Other";

        builder.topic(topic);
    }

    protected String buildTraits(Map<String, String> traits, SNSEvent.SNSRecord record) {
        attributesToTraits(traits, record);
        messageToTraits(traits, record.getSNS());
        return record.getSNS().getSubject();
    }

    protected void messageToTraits(Map<String, String> traits, SNSEvent.SNS sns) {

        String message = sns.getMessage();
        String[] parts = message.split("\n");

        if (parts.length == 0) {
            traits.put("Message", message);
            return;
        }

        int unknown = 0;

        for (String source : parts) {
            int pos = source.indexOf(": ");
            if (pos < 0) {
                if (source.length() > 0) {
                    String key = "Unknown-" + (++unknown);
                    traits.put(key, source);
                }
            } else {
                String key = source.substring(0, pos).replace(" ", "-");
                String value = source.substring(pos + 2);
                traits.put(key, value);
            }
        }
    }

    protected void attributesToTraits(Map<String, String> traits, SNSEvent.SNSRecord record) {
        for (Map.Entry<String, SNSEvent.MessageAttribute> entry: record.getSNS().getMessageAttributes().entrySet()) {

            String key = entry.getKey();
            SNSEvent.MessageAttribute attribute = entry.getValue();

            String value = attribute.getValue();
            traits.put(key, value);
        }
    }
}
