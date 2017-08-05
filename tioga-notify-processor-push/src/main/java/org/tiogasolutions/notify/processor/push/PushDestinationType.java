package org.tiogasolutions.notify.processor.push;

public enum PushDestinationType {

    sesEmailMsg, smtpEmailMsg, smsMsg, phoneCall, jabberMsg;

    PushDestinationType() {
    }

    public boolean isSesEmailMsg() {
        return this == sesEmailMsg;
    }

    public boolean isSmtpEmailMsg() {
        return this == smtpEmailMsg;
    }

    public boolean isSmsMsg() {
        return this == smsMsg;
    }

    public boolean isJabberMsg() {
        return this == jabberMsg;
    }

    public boolean isPhoneCall() {
        return this == phoneCall;
    }
}
