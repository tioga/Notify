package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.sender.lambda.pipeline.CodePipelineApprovalMsg;
import org.tiogasolutions.notify.sender.lambda.sns.SnsRecord;

import static org.tiogasolutions.dev.common.StringUtils.isNotBlank;
import static org.tiogasolutions.notify.sender.lambda.pipeline.CodePipelineApprovalMsg.*;

public class LambdaNotifierCodePipelineApproval extends LambdaSnsNotifier {

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

            Approval approval  = codePipelineApprovalMsg.getApproval();

            // Basic traits...
            builder.trait("region",             codePipelineApprovalMsg.getRegion());
            builder.trait("consoleLink",        codePipelineApprovalMsg.getConsoleLink());

            // Approval traits...
            builder.trait("pipelineName",       approval.getPipelineName());
            builder.trait("stageName",          approval.getStageName());
            builder.trait("actionName",         approval.getActionName());
            builder.trait("token",              approval.getToken());
            builder.trait("expires",            approval.getExpires());
            builder.trait("externalEntityLink", approval.getExternalEntityLink());
            builder.trait("customData",         approval.getCustomData());

            summary = String.format("Approval to <%s|%s> is required for the pipeline %s.",
                    codePipelineApprovalMsg.getConsoleLink(),
                    approval.getActionName().toLowerCase().replace("-", " "),
                    approval.getPipelineName()
            );

            if (isNotBlank(approval.getCustomData())) {
                summary += String.format("\n%s", approval.getCustomData());
            }

            if (isNotBlank(approval.getCustomData())) {
                summary += String.format("\nSee also <%s>", approval.getExternalEntityLink());
            }
        }
    }
}
