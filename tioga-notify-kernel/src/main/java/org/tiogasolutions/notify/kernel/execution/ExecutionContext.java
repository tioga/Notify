package org.tiogasolutions.notify.kernel.execution;

import org.tiogasolutions.notify.pub.domain.DomainProfile;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

/**
 * User: Harlan
 * Date: 2/9/2015
 * Time: 11:14 PM
 */
public class ExecutionContext {
  private final String apiKey;
  private final String domainName;
  private final HttpHeaders headers;
  private final UriInfo uriInfo;

  public ExecutionContext(DomainProfile domainProfile) {
    this(domainProfile, null, null);
  }

  public ExecutionContext(DomainProfile domainProfile, UriInfo uriInfo, HttpHeaders headers) {
    this.apiKey = domainProfile.getApiKey();
    this.domainName = domainProfile.getDomainName();
    this.uriInfo = uriInfo;
    this.headers = headers;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getDomainName() {
    return domainName;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public UriInfo getUriInfo() {
    return uriInfo;
  }
}
