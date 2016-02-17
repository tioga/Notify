package org.tiogasolutions.notify.extras.pwsmon.pub;

public class Prompts {

  private String[] username = new String[2];
  private String[] password = new String[2];

  public Prompts(String username, String password) {
    this.username[0] = "text";
    this.username[1] = username;

    this.password[0] = "password";
    this.password[1] = password;
  }

  public String[] getUsername() {
    return username;
  }

  public String[] getPassword() {
    return password;
  }
}
