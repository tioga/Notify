package org.tiogasolutions.notify.processor.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.tiogasolutions.dev.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by harlan on 3/9/15.
 */
public class SlackMessage {
  private static Logger log = LoggerFactory.getLogger(SlackMessage.class);
  private String username;
  private String channel;
  private String text;
  private String iconUrl;
  private String iconEmoji;

  public String getUsername() {
    return username;
  }

  public SlackMessage setUsername(String username) {
    this.username = username;
    return this;
  }

  public String getChannel() {
    return channel;
  }

  public SlackMessage setChannel(String channel) {
    this.channel = channel;
    return this;
  }

  public String getText() {
    return text;
  }

  public SlackMessage setText(String text) {
    this.text = text;
    return this;
  }

  @JsonProperty("icon_url")
  public String getIconUrl() {
    return iconUrl;
  }

  public SlackMessage setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
    return this;
  }

  @JsonProperty("icon_emoji")
  public String getIconEmoji() {
    return iconEmoji;
  }

  public SlackMessage setIconEmoji(String iconEmoji) {
    this.iconEmoji = iconEmoji;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SlackMessage message = (SlackMessage) o;

    if (channel != null ? !channel.equals(message.channel) : message.channel != null) return false;
    if (iconEmoji != null ? !iconEmoji.equals(message.iconEmoji) : message.iconEmoji != null) return false;
    if (iconUrl != null ? !iconUrl.equals(message.iconUrl) : message.iconUrl != null) return false;
    if (text != null ? !text.equals(message.text) : message.text != null) return false;
    if (username != null ? !username.equals(message.username) : message.username != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = username != null ? username.hashCode() : 0;
    result = 31 * result + (channel != null ? channel.hashCode() : 0);
    result = 31 * result + (text != null ? text.hashCode() : 0);
    result = 31 * result + (iconUrl != null ? iconUrl.hashCode() : 0);
    result = 31 * result + (iconEmoji != null ? iconEmoji.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SlackMessage{" +
        "userName='" + username + '\'' +
        ", channel='" + channel + '\'' +
        ", text='" + text + '\'' +
        ", iconUrl='" + iconUrl + '\'' +
        ", iconEmoji='" + iconEmoji + '\'' +
        '}';
  }
}
