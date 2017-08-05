package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.sender.lambda.pub.build.CodeBuildStateChangeMsg;
import org.tiogasolutions.notify.sender.lambda.pub.build.Detail;
import org.tiogasolutions.notify.sender.lambda.pub.sns.SnsRecord;

public class LambdaNotifierCodeBuildStateChange extends LambdaNotifier {

    @Override
    public LambdaNotifier.Processor createProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record) {
        return new CodeBuildProcessor(om, logger, notifier, context, record, "Build Status");
    }

    public class CodeBuildProcessor extends LambdaNotifier.Processor {

        private CodeBuildStateChangeMsg codeBuildStateChangeMsg;

        public CodeBuildProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record, String topicName) {
            super(om, logger, notifier, context, record, topicName);
        }

        @Override
        protected void processPayload() throws Exception {
            String json = record.getSns().getMessage();
            codeBuildStateChangeMsg = om.readValue(json, CodeBuildStateChangeMsg.class);
        }

        @Override
        protected void decorateNotification() {

            // Status Change attributes
            builder.trait("version", codeBuildStateChangeMsg.getVersion());
            builder.trait("id", codeBuildStateChangeMsg.getId());
            builder.trait("detail_type", codeBuildStateChangeMsg.getDetailType());
            builder.trait("source", codeBuildStateChangeMsg.getSource());
            builder.trait("account", codeBuildStateChangeMsg.getAccount());
            builder.trait("time", codeBuildStateChangeMsg.getTime());
            builder.trait("region", codeBuildStateChangeMsg.getRegion());

            for (int i = 0; i < codeBuildStateChangeMsg.getResources().size(); i++) {
                String resource = codeBuildStateChangeMsg.getResources().get(i);
                builder.trait("resources-"+i, resource);
            }

            // Status Change Detail attributes
            Detail detail = codeBuildStateChangeMsg.getDetail();
            builder.trait("build_status", detail.getBuildStatus());
            builder.trait("project_name", detail.getProjectName());
            builder.trait("build_id", detail.getBuildId());
            builder.trait("current_phase", detail.getCurrentPhase());
            builder.trait("current_phase_context", detail.getCurrentPhaseContext());
            builder.trait("detail_version", detail.getVersion());

            // Customize the message
            String status = detail.getBuildStatus();
            String project = detail.getProjectName();

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
