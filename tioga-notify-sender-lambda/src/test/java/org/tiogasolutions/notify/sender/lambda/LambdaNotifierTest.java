package org.tiogasolutions.notify.sender.lambda;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;

@Test
public class LambdaNotifierTest {

    private Context context;
    private LambdaNotifier notifier;

    @BeforeClass
    public void beforeClass() {
        System.setProperty("NOTIFIER_URL", "https://notify-engine.tioga.solutions/api/v2/requests");
        System.setProperty("NOTIFIER_USERNAME", "ZKPCW-6939949");
        System.setProperty("NOTIFIER_PASSWORD", "GoFish");

        notifier = new LambdaNotifier();

        context = new Context() {
            @Override
            public String getAwsRequestId() {
                return null;
            }

            @Override
            public String getLogGroupName() {
                return null;
            }

            @Override
            public String getLogStreamName() {
                return null;
            }

            @Override
            public String getFunctionName() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public String getInvokedFunctionArn() {
                return null;
            }

            @Override
            public CognitoIdentity getIdentity() {
                return null;
            }

            @Override
            public ClientContext getClientContext() {
                return null;
            }

            @Override
            public int getRemainingTimeInMillis() {
                return 0;
            }

            @Override
            public int getMemoryLimitInMB() {
                return 0;
            }

            @Override
            public LambdaLogger getLogger() {
                return new LambdaLogger() {
                    @Override
                    public void log(String string) {
                        System.out.print("Lambda Msg: " + string);
                    }
                };
            }
        };
    }

    public void testSomething() {

        SNSEvent.SNSRecord record = new SNSEvent.SNSRecord();
        record.setEventVersion("1.0");

        SNSEvent.SNS sns = new SNSEvent.SNS();
        sns.setSignatureVersion("1");
        sns.setTimestamp(new DateTime());
        sns.setSignature("EXAMPLE");
        sns.setSigningCertUrl("EXAMPLE");
        sns.setMessageId("95df01b4-ee98-5cb9-9903-4c221d41eb5e");
        sns.setMessage("Timestamp: Tue Aug 01 05:22:31 UTC 2017\nMessage: Environment health has transitioned from Ok to Info. Configuration update in progress (running for 7 seconds).\n\nEnvironment: dnl-engine-prod\nApplication: dnl-engine\n\nEnvironment URL: http://dnl-engine-prod.us-west-2.elasticbeanstalk.com\nNotificationProcessId: b28d277e-0ff3-45d6-800e-f2e997eda4b7");
        sns.setType("Notification");
        sns.setUnsubscribeUrl("EXAMPLE");
        sns.setTopicArn("arn:aws:sns:EXAMPLE");
        sns.setSubject("TestInvoke");

        Map<String, SNSEvent.MessageAttribute> attributes = new HashMap<>();

        SNSEvent.MessageAttribute attribute = new SNSEvent.MessageAttribute();
        attribute.setType("String");
        attribute.setValue("TestString");
        attributes.put("Test", attribute);
        sns.setMessageAttributes(attributes);

        attribute = new SNSEvent.MessageAttribute();
        attribute.setType("TestBinary");
        attribute.setValue("Binary");
        attributes.put("TestBinary", attribute);
        sns.setMessageAttributes(attributes);

        record.setSns(sns);
        record.setEventSubscriptionArn("arn:aws:sns:EXAMPLE");
        record.setEventSource("aws:sns");

        SNSEvent request = new SNSEvent();
        request.setRecords(singletonList(record));

        notifier.handleRequest(request, context);
    }
}