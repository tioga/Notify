package org.tiogasolutions.notify.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 5:28 PM
 */
public class AttachmentInfo {
  private final String name;
  private final String contentType;

  public AttachmentInfo(@JsonProperty("name") String name, @JsonProperty("contentType") String contentType) {
    this.name = name;
    this.contentType = contentType;
  }

  public String getName() {
      return name;
  }

  public String getContentType() {
      return contentType;
  }
}
