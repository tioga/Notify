package org.tiogasolutions.notify.kernel.message;

import org.tiogasolutions.dev.common.StringUtils;

/**
 * Created by jacobp on 3/19/2015.
 */
public class HtmlMessage {

  private final String html;
  private final String subject;
  private final String body;

  public HtmlMessage(String html) {
    this.html = html;
    this.body = extractBody(html);
    this.subject = extractSubject(html);
  }

  public String getHtml() {
    return html;
  }

  public String getSubject() {
    return subject;
  }

  public String getBody() {
    return body;
  }

  public String extractBody(String htmlContent) {
    String html = StringUtils.getTagContents(htmlContent, "html", 0);
    return StringUtils.getTagContents(html, "body", 0);
  }

  public String extractSubject(String htmlContent) {
    String html = StringUtils.getTagContents(htmlContent, "html", 0);
    String head = StringUtils.getTagContents(html, "head", 0);
    return StringUtils.getTagContents(head, "title", 0);
  }
}
