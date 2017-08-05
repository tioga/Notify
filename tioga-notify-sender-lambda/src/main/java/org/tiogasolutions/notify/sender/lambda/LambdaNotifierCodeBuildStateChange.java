package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.sender.lambda.pub.build.CodeBuildStateChange;
import org.tiogasolutions.notify.sender.lambda.pub.sns.SnsRecord;

public class LambdaNotifierCodeBuildStateChange extends LambdaNotifier {

    @Override
    public LambdaNotifier.Processor createProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record) {
        return new CodeBuildProcessor(om, logger, notifier, context, record, "Build Status");
    }

    public class CodeBuildProcessor extends LambdaNotifier.Processor {

        private CodeBuildStateChange stateChange;

        public CodeBuildProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record, String topicName) {
            super(om, logger, notifier, context, record, topicName);
        }

        @Override
        protected void processPayload() throws Exception {
            String json = record.getSns().getMessage();
            stateChange = om.readValue(json, CodeBuildStateChange.class);
        }

        @Override
        protected void decorateNotification() {
            String status = stateChange.getDetail().getBuildStatus();
            String project = stateChange.getDetail().getProjectName();

            builder.trait("buildstatus", status);
            builder.trait("projectname", project);

            if ("IN_PROGRESS".equals(status)) {
                summary = String.format("The build for %s has started.", project, status);
            } else if ("FAILED".equals(status)) {
                summary = String.format("The build of %s has failed.", project, status);
            } else if ("SUCCEEDED".equals(status)) {
                summary = String.format("The build of %s has succeeded.", project, status);
            } else {
                summary = String.format("Build status for %s is %s.", project, status);
            }
        }
    }
}
