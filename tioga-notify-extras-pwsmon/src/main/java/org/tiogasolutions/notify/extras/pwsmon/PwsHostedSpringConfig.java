package org.tiogasolutions.notify.extras.pwsmon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.runners.grizzly.GrizzlyServerConfig;

@Profile("hosted")
@Configuration
public class PwsHostedSpringConfig {

  private String getContext() {
    return EnvUtils.findProperty("pws.context", "");
  }

  private int getPort() {
    String value = EnvUtils.findProperty("pws.port", "8080");
    return Integer.valueOf(value);
  }

  private int getShutdownPort() {
    String value = EnvUtils.findProperty("pws.shutdownPort", "8081");
    return Integer.valueOf(value);
  }

  private String getHostName() {
    return EnvUtils.findProperty("pws.hostName", "localhost");
  }

  @Bean(name="org.tiogasolutions.runners.grizzly.GrizzlyServerConfig")
  public GrizzlyServerConfig grizzlyServerConfig() {
    GrizzlyServerConfig config = new GrizzlyServerConfig();
    config.setHostName(getHostName());
    config.setPort(getPort());
    config.setShutdownPort(getShutdownPort());
    config.setContext(getContext());
    config.setToOpenBrowser(false);
    return config;
  }
}
