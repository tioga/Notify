package org.tiogasolutions.notify.sender.lambda.sns;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

// @JsonIgnoreProperties(ignoreUnknown = true)
public class Sns {

    private final Map<String, MessageAttribute> messageAttributes;

    private final String signingCertUrl;
    private final String messageId;
    private final String message;
    private final String subject;
    private final String unsubscribeUrl;
    private final String type;
    private final String signatureVersion;
    private final String signature;
    private final String timestamp;
    private final String topicArn;

    public Sns(@JsonProperty("MessageAttributes") Map<String, MessageAttribute> messageAttributes,
               @JsonProperty("SigningCertUrl") String signingCertUrl,
               @JsonProperty("MessageId") String messageId,
               @JsonProperty("Message") String message,
               @JsonProperty("Subject") String subject,
               @JsonProperty("UnsubscribeUrl") String unsubscribeUrl,
               @JsonProperty("Type") String type,
               @JsonProperty("SignatureVersion") String signatureVersion,
               @JsonProperty("Signature") String signature,
               @JsonProperty("Timestamp") String timestamp,
               @JsonProperty("TopicArn") String topicArn) {

        this.messageAttributes = messageAttributes;
        this.signingCertUrl = signingCertUrl;
        this.messageId = messageId;
        this.message = message;
        this.subject = subject;
        this.unsubscribeUrl = unsubscribeUrl;
        this.type = type;
        this.signatureVersion = signatureVersion;
        this.signature = signature;
        this.timestamp = timestamp;
        this.topicArn = topicArn;
    }

    public Map<String, MessageAttribute> getMessageAttributes() {
        return messageAttributes;
    }

    public String getSigningCertUrl() {
        return signingCertUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getUnsubscribeUrl() {
        return unsubscribeUrl;
    }

    public String getType() {
        return type;
    }

    public String getSignatureVersion() {
        return signatureVersion;
    }

    public String getSignature() {
        return signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTopicArn() {
        return topicArn;
    }
}
