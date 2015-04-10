package org.tiogasolutions.notify.processor.push;

public enum PushDestinationType {

  emailMsg, smsMsg, phoneCall, jabberMsg;

  PushDestinationType() {
  }

  public boolean isEmailMsg() {
    return this == emailMsg;
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
