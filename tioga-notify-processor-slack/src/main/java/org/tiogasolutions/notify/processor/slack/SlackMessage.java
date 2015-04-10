package org.tiogasolutions.notify.processor.slack;

import org.tiogasolutions.dev.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by harlan on 3/9/15.
 */
public class SlackMessage {
  private static Logger log = LoggerFactory.getLogger(SlackMessage.class);
  private String userName;
  private String channel;
  private String text;
  private String iconUrl;
  private String iconEmoji;

  private static String field(String fieldName, String value) {
    return String.format("\"%s\":\"%s\",", fieldName, value);
  }

  public String toJson() {
    StringBuilder sb = new StringBuilder("{");

    if (StringUtils.isNotBlank(userName)) {
      sb.append(field("username", userName));
    }
    if (StringUtils.isNotBlank(channel)) {
      sb.append(field("channel", channel));
    }
    if (StringUtils.isNotBlank(text)) {
      // HACK - when using template from file system was ending up with a trailing newline in the text.
      if (text.endsWith("\n")) {
        text = text.substring(0, text.length()-1);
      }
      sb.append(field("text", text));
    }
    if (StringUtils.isNotBlank(iconUrl)) {
      sb.append(field("icon_url", iconUrl));
    }
    if (StringUtils.isNotBlank(iconEmoji)) {
      sb.append(field("icon_emoji", iconEmoji));
    }
    int lastIndex = sb.length() - 1;
    if (sb.charAt(lastIndex) == ',') {
      sb.deleteCharAt(lastIndex);
    }

    sb.append("}");

    String json = sb.toString();

    if (log.isTraceEnabled()) {
      log.trace("Message json [" + json + "]");
    }
    return json;
  }

  public String getUserName() {
    return userName;
  }

  public SlackMessage setUserName(String userName) {
    this.userName = userName;
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

  public String getIconUrl() {
    return iconUrl;
  }

  public SlackMessage setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
    return this;
  }

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
    if (userName != null ? !userName.equals(message.userName) : message.userName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = userName != null ? userName.hashCode() : 0;
    result = 31 * result + (channel != null ? channel.hashCode() : 0);
    result = 31 * result + (text != null ? text.hashCode() : 0);
    result = 31 * result + (iconUrl != null ? iconUrl.hashCode() : 0);
    result = 31 * result + (iconEmoji != null ? iconEmoji.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SlackMessage{" +
        "userName='" + userName + '\'' +
        ", channel='" + channel + '\'' +
        ", text='" + text + '\'' +
        ", iconUrl='" + iconUrl + '\'' +
        ", iconEmoji='" + iconEmoji + '\'' +
        '}';
  }
}
