package org.tiogasolutions.notify.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.tiogasolutions.dev.common.BeanUtils;
import org.tiogasolutions.dev.jackson.TiogaJacksonTranslator;
import org.tiogasolutions.notify.NotifyObjectMapper;
import org.tiogasolutions.notify.kernel.config.CouchEnvironment;
import org.tiogasolutions.notify.kernel.config.CouchServersConfig;
import org.tiogasolutions.notify.kernel.config.SystemConfiguration;
import org.tiogasolutions.notify.kernel.config.TrustedUserStore;

import static org.tiogasolutions.dev.common.StringUtils.isNotBlank;

@Profile("test")
@Configuration
public class SpringTestConfig {

    @Bean
    public NotifyObjectMapper notifyObjectMapper() {
        return new NotifyObjectMapper();
    }

    @Bean
    public TiogaJacksonTranslator tiogaJacksonTranslator(NotifyObjectMapper objectMapper) {
        return new TiogaJacksonTranslator(objectMapper);
    }

    @Bean
    public CouchServersConfig couchServersConfig() {

        CouchServersConfig config = new CouchServersConfig();

        String couchUrl = "http://127.0.0.1:5984";
        String username = "test-user";
        String password = "test-user";

        if (isNotBlank(System.getenv("awsCouchUrl"))) {
            couchUrl = System.getenv("awsCouchUrl");
        }
        if (isNotBlank(System.getenv("awsCouchUsername"))) {
            username = System.getenv("awsCouchUsername");
        }
        if (isNotBlank(System.getenv("awsCouchPassword"))) {
            password = System.getenv("awsCouchPassword");
        }

        config.setMasterUrl(couchUrl);
        config.setMasterUsername(username);
        config.setMasterPassword(password);
        config.setMasterDatabaseName("test-notify-master");

        config.setNotificationUrl(couchUrl);
        config.setNotificationUsername(username);
        config.setNotificationPassword(password);
        config.setNotificationDatabasePrefix("test-notify-");
        config.setNotificationDatabaseSuffix("-notification");

        config.setRequestUrl(couchUrl);
        config.setRequestUsername(username);
        config.setRequestPassword(password);
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
        return new SystemConfiguration("*", "/api/v1/client", "/api/v1/admin");
    }

    @Bean
    public CouchEnvironment couchEnvironment() {
        return new CouchEnvironment().setTesting(true);
    }
}
