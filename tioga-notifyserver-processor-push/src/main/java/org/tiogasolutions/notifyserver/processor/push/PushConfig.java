package org.tiogasolutions.notifyserver.processor.push;

public class PushConfig {

  private String emailFromAddress;
  private String smsFromNumber;
  private String phoneFromNumber;

  public PushConfig() {
  }

  public String getEmailFromAddress() {
    return emailFromAddress;
  }

  public void setEmailFromAddress(String emailFromAddress) {
    this.emailFromAddress = emailFromAddress;
  }

  public String getSmsFromNumber() {
    return smsFromNumber;
  }

  public void setSmsFromNumber(String smsFromNumber) {
    this.smsFromNumber = smsFromNumber;
  }

  public String getPhoneFromNumber() {
    return phoneFromNumber;
  }

  public void setPhoneFromNumber(String phoneFromNumber) {
    this.phoneFromNumber = phoneFromNumber;
  }
}
