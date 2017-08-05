package org.tiogasolutions.notify.sender.http;

/**
 * User: Harlan
 * Date: 1/27/2015
 * Time: 1:20 AM
 */
public class HttpNotificationSenderConfig {
    private String url;
    private String userName;
    private String password;
    private SslConfig sslConfig;

    public String getUrl() {
        return url;
    }

    public HttpNotificationSenderConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public HttpNotificationSenderConfig setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public HttpNotificationSenderConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public HttpNotificationSenderConfig setSslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
        return this;
    }
}
