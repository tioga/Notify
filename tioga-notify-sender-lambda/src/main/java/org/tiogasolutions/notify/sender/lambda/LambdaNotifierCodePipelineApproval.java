package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.sender.lambda.pub.pipeline.Approval;
import org.tiogasolutions.notify.sender.lambda.pub.pipeline.CodePipelineApprovalMsg;
import org.tiogasolutions.notify.sender.lambda.pub.sns.SnsRecord;

public class LambdaNotifierCodePipelineApproval extends LambdaNotifier {

    @Override
    public Processor createProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record) {
        return new PipelineProcessor(om, logger, notifier, context, record, "AWS CodePipeline Approval");
    }

    public class PipelineProcessor extends Processor {

        private CodePipelineApprovalMsg codePipelineApprovalMsg;

        public PipelineProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record, String topicName) {
            super(om, logger, notifier, context, record, topicName);
        }

        @Override
        protected void processPayload() throws Exception {
            String json = record.getSns().getMessage();
            codePipelineApprovalMsg = om.readValue(json, CodePipelineApprovalMsg.class);
        }

        @Override
        protected void decorateNotification() {

            // Message attributes
            builder.trait("region", codePipelineApprovalMsg.getRegion());
            builder.trait("consoleLink", codePipelineApprovalMsg.getConsoleLink());

            Approval approval = codePipelineApprovalMsg.getApproval();
            builder.trait("pipelineName", approval.getPipelineName());
            builder.trait("stageName", approval.getStageName());
            builder.trait("actionName", approval.getActionName());
            builder.trait("token", approval.getToken());
            builder.trait("expires", approval.getExpires());
            builder.trait("externalEntityLink", approval.getExternalEntityLink());
            builder.trait("customData", approval.getCustomData());

            summary = String.format("Approval to \"%s\" is required for the pipeline %s.",
                    approval.getActionName().toLowerCase().replace("-", " "),
                    approval.getPipelineName()
            );
        }
    }
}
