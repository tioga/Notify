package org.tiogasolutions.notify.extras.pwsmon.pws.pub;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {

  private final String tokenType;
  private final String accessToken;
  private final String refreshToken;
  private final int expiresIn;
  private final String scope;
  private final String jti;

  public LoginResponse(@JsonProperty("token_type") String tokenType,
                       @JsonProperty("access_token") String accessToken,
                       @JsonProperty("refresh_token") String refreshToken,
                       @JsonProperty("expires_in") int expiresIn,
                       @JsonProperty("scope") String scope,
                       @JsonProperty("jti") String jti) {

    this.tokenType = tokenType;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresIn = expiresIn;
    this.scope = scope;
    this.jti = jti;
  }

  public String getTokenType() {
    return tokenType;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public int getExpiresIn() {
    return expiresIn;
  }

  public String getScope() {
    return scope;
  }

  public String getJti() {
    return jti;
  }
}
