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
import java.util.Random;

public class LambdaNotifier implements RequestHandler<SNSEvent, Object> {

    private Notifier notifier;

    public LambdaNotifier() {

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
        this.notifier = new Notifier(sender);
    }

    private void log(Context context, String msg) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.sql.Timestamp(System.currentTimeMillis()));
        context.getLogger().log("["+timestamp+"] " + msg.trim() + "\n");
    }

//    public Object handleRequest(Map map, Context context){
//        log(context, "Invocation started.");
//
//        log(context, map.toString());
//
//        String msg = map.toString();
//        NotificationBuilder builder = notifier.begin().summary(msg);
//
//        try {
//            SendNotificationResponse response = builder.send().get();
//
//            if (response.getResponseType() == SendNotificationResponseType.FAILURE) {
//                StringWriter writer = new StringWriter();
//                response.getThrowable().printStackTrace(new PrintWriter(writer));
//                return writer.toString();
//
//            } else {
//                return map.toString();
//            }
//        } catch (Exception e) {
//            StringWriter writer = new StringWriter();
//            e.printStackTrace(new PrintWriter(writer));
//            return writer.toString();
//
//        } finally {
//            log(context, "Invocation completed.");
//        }
//    }

    public Object handleRequest(SNSEvent request, Context context){
        log(context, "Invocation started.");

        SNSEvent.SNSRecord record = request.getRecords().get(0);
        return handleRequest(record, context);
    }

    private Object handleRequest(SNSEvent.SNSRecord record, Context context) {
        // May re-assign later.
        String subject = record.getSNS().getSubject();


        String message = record.getSNS().getMessage();
        Map<String,String> traits = new LinkedHashMap<>();

        for (String source : message.split("\n")) {
            if (source.length() == 0) {
                continue;
            }
            int pos = source.indexOf(": ");
            if (pos < 0) {
                traits.put("Unknown-" + new Random(System.currentTimeMillis()).nextInt(10000), source);
            } else {
                String key = source.substring(0, pos).replace(" ", "-");
                String value = source.substring(pos + 2);
                traits.put(key, value);

                if ("Message".equals(key)) {
                    subject = value;
                }
            }
        }

        for (Map.Entry<String, SNSEvent.MessageAttribute> attribute : record.getSNS().getMessageAttributes().entrySet()) {
            String key  = attribute.getKey();
            String value = attribute.getValue().getValue();
            traits.put(key, value);
        }

        NotificationBuilder builder = notifier.begin().summary(subject);
        log(context, "Subject: " + subject);

        for (Map.Entry<String, String> entry : traits.entrySet()) {
            builder.trait(entry.getKey(), entry.getValue());
            log(context, String.format("  %s: %s", entry.getKey(), entry.getValue()));
        }

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
}
