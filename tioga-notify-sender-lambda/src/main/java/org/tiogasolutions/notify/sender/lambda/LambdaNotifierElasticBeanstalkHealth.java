package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.tiogasolutions.notify.notifier.Notifier;
import org.tiogasolutions.notify.sender.lambda.pub.sns.SnsRecord;

public class LambdaNotifierElasticBeanstalkHealth extends LambdaNotifier {

    @Override
    public LambdaNotifier.Processor createProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record) {
        return new ElasticBeanstalkProcessor(om, logger, notifier, context, record);
    }

    public class ElasticBeanstalkProcessor extends LambdaNotifier.Processor {

        public ElasticBeanstalkProcessor(ObjectMapper om, Logger logger, Notifier notifier, Context context, SnsRecord record) {
            super(om, logger, notifier, context, record, "AWS Elastic Beanstalk");
        }

        @Override
        protected void processPayload() {
            // Parse the new-line delineated message.
            String message = record.getSns().getMessage();
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
                    if ("Message".equals(key)) {
                        summary = value;
                    } else {
                        traits.put(key, value);
                    }
                }
            }
        }

        @Override
        protected void decorateNotification() {
            if (summary != null && summary.startsWith("Environment health has transitioned from ")) {
                if (summary.contains(" to Severe.")) {
                    builder.trait("elasticBeanstalkStatus", "severe");
                } else if (summary.contains(" to Info.")) {
                    builder.trait("elasticBeanstalkStatus", "info");
                } else if (summary.contains(" to Ok.")) {
                    builder.trait("elasticBeanstalkStatus", "ok");
                } else {
                    builder.trait("elasticBeanstalkStatus", "unknown");
                }
            } else {
                builder.trait("elasticBeanstalkStatus", "unknown");
            }
        }
    }
}
