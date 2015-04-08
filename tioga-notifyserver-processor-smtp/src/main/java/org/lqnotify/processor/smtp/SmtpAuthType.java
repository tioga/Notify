/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package org.lqnotify.processor.smtp;

public enum SmtpAuthType {
    standard(25, "Standard"),
    ssl(465, "SSL"),
    tls(587, "TLS");

  private final String defaultPort;
  private final String label;

  private SmtpAuthType(int defaultPort, String label) {
    this.defaultPort = String.valueOf(defaultPort);
    this.label = String.format("%s (%s)", label, defaultPort);
  }

  public String getLabel() {
    return label;
  }

  public boolean isStandard() {
    return this == standard;
  }

  public boolean isSsl() {
    return this == ssl;
  }

  public boolean isTls() { return this == tls; }

  public String getDefaultPort() {
    return defaultPort;
  }
}
