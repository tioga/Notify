package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.notifier.send.NotificationSender;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;
import org.tiogasolutions.notify.notifier.send.SendNotificationResponseType;
import org.tiogasolutions.notify.sender.http.HttpNotificationSender;
import org.tiogasolutions.notify.sender.http.HttpNotificationSenderConfig;
import org.tiogasolutions.notify.sender.lambda.pipeline.CodePipelineJob;
import org.tiogasolutions.notify.sender.lambda.pipeline.DirectMessage;

import java.io.*;

public class LambdaNotifierCodePipelineMessage implements RequestStreamHandler {

    protected final Notifier notifier;
    protected final ObjectMapper om = new ObjectMapper();

    public LambdaNotifierCodePipelineMessage() {
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

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        Logger logger = new Logger(context);
        logger.log("Invocation started: " + this);

        try {
            String json = IoUtils.toString(inputStream);
            logger.log(json);

            json = json.replace("CodePipeline.job", "CodePipelineJob");
            CodePipelineJob job = om.readValue(json, CodePipelineJob.class);
            logger.log("Job: " + job);

            DirectMessage message = om.readValue(job.getUserParameters(), DirectMessage.class);

            SendNotificationResponse response = notifier.begin()
                    .topic(message.getTopic())
                    .summary(message.getSummary())
                    .send().get();

            logger.log("Sent notification: " + response.getResponseType());

            if (response.getResponseType() == SendNotificationResponseType.FAILURE) {
                throw response.getThrowable();
            }

        } catch (Throwable e) {
            StringWriter pw = new StringWriter();
            e.printStackTrace(new PrintWriter(pw));
            logger.log("Unexpected Exception: " + pw);

        }

        logger.log("Invocation completed.");
    }
}
