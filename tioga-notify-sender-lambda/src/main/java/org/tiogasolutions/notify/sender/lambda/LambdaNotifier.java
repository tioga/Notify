package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.builder.NotificationBuilder;
import org.tiogasolutions.notify.notifier.send.NotificationSender;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponseType;
import org.tiogasolutions.notify.sender.http.HttpNotificationSender;
import org.tiogasolutions.notify.sender.http.HttpNotificationSenderConfig;
import org.tiogasolutions.notify.sender.lambda.pub.sns.MessageAttribute;
import org.tiogasolutions.notify.sender.lambda.pub.sns.SnsEvent;
import org.tiogasolutions.notify.sender.lambda.pub.sns.SnsRecord;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class LambdaNotifier implements RequestStreamHandler {

    protected final Notifier notifier;
    protected final ObjectMapper om = new ObjectMapper();

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
        notifier = new Notifier(sender);
    }

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Logger logger = new Logger(context);
        logger.log("Invocation started: " + this);

        try {
            String json = IoUtils.toString(inputStream);
            logger.log(json);

            SnsEvent event = om.readValue(json, SnsEvent.class);

            for (SnsRecord record : event.getRecords()) {
                Processor processor = createProcessor(om, logger, notifier, context, record);
                processor.processRecord();
            }

        } catch (Throwable e) {
            StringWriter pw = new StringWriter();
            e.printStackTrace(new PrintWriter(pw));
            logger.log("Unexpected Exception: " + pw);

        } finally {
            logger.log("Invocation completed.");
        }
    }

    public Processor createProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record) {
        return new Processor(om, logger, notifier, context, record, "AWS Other");
    }

    public class Processor {

        protected final ObjectMapper om;
        protected final Logger logger;
        protected final Notifier notifier;
        protected final NotificationBuilder builder;
        protected final Context context;
        protected final SnsRecord record;
        protected final String defaultTopic;

        protected final Map<String, String> traits = new LinkedHashMap<>();

        protected String topic;
        protected String summary;

        public Processor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record, String defaultTopic) {
            this.om = om;
            this.logger = logger;
            this.notifier = notifier;
            this.context = context;
            this.record = record;
            this.builder = notifier.begin();
            this.defaultTopic = defaultTopic;
        }

        public void processRecord() throws Throwable {
            buildTraitsMap();
            topic = getTopic();
            summary = getSummary();

            processPayload();
            decorateNotification();

            builder.topic(topic);
            builder.summary(summary);

            for (Map.Entry<String, String> entry : traits.entrySet()) {
                builder.trait(entry.getKey(), entry.getValue());
                logger.log(String.format("  %s: %s", entry.getKey(), entry.getValue()));
            }

            SendNotificationResponse response = builder.send().get();
            logger.log("Sent notification: " + summary);

            if (response.getResponseType() == SendNotificationResponseType.FAILURE) {
                throw response.getThrowable();
            }
        }

        protected String getSummary() {
            return record.getSns().getSubject();
        }

        protected String getTopic() {
            String topic = System.getProperty("NOTIFIER_TOPIC");
            if (topic == null) topic = System.getenv("NOTIFIER_TOPIC");
            if (topic == null) topic = defaultTopic;
            return topic;
        }

        protected void buildTraitsMap() {
            for (Map.Entry<String, MessageAttribute> entry : record.getSns().getMessageAttributes().entrySet()) {

                String key = entry.getKey();
                MessageAttribute attribute = entry.getValue();

                String value = attribute.getValue();
                traits.put(key, value);
            }
        }

        protected void processPayload() throws Exception {
        }

        protected void decorateNotification() throws Exception {
        }
    }
}
