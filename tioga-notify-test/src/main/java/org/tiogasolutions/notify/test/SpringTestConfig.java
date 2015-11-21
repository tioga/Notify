package org.tiogasolutions.notify.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.kernel.config.CouchEnvironment;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;
import org.tiogasolutions.notify.kernel.config.SystemConfiguration;
import org.tiogasolutions.notify.kernel.config.TrustedUserStore;

@Profile("test")
@Configuration
public class SpringTestConfig {

  @Bean
  public TiogaJacksonObjectMapper tiogaJacksonObjectMapper() {
    return new TiogaJacksonObjectMapper();
  }

  @Bean
  public TiogaJacksonTranslator tiogaJacksonTranslator(TiogaJacksonObjectMapper objectMapper) {
    return new TiogaJacksonTranslator(objectMapper);
  }

  @Bean
  public CouchServersConfig couchServersConfig() {

    CouchServersConfig config = new CouchServersConfig();

    config.setMasterUrl("http://127.0.0.1:5984");
    config.setMasterUserName("test-user");
    config.setMasterPassword("test-user");
    config.setMasterDatabaseName("test-notify-master");

    config.setNotificationUrl("http://127.0.0.1:5984");
    config.setNotificationUserName("test-user");
    config.setNotificationPassword("test-user");
    config.setNotificationDatabasePrefix("test-notify-");
    config.setNotificationDatabaseSuffix("-notification");

    config.setRequestUrl("http://127.0.0.1:5984");
    config.setRequestUserName("test-user");
    config.setRequestPassword("test-user");
    config.setRequestDatabasePrefix("test-notify-");
    config.setRequestDatabaseSuffix("-request");

    return config;
  }

  @Bean
  public TrustedUserStore trustedUserStore() {
    return new TrustedUserStore(BeanUtils.toMap(
      "admin:Testing123"
    ));
  }

  @Bean
  public SystemConfiguration systemConfiguration() {
    SystemConfiguration configuration = new SystemConfiguration();
    configuration.setAccessControlAllowOrigin("http://www.localhost:63342");
    return configuration;
  }

  @Bean
  public CouchEnvironment couchEnvironment() {
    return CouchEnvironment.test();
  }
}
