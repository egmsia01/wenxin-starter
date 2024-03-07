package com.gearwenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ge Mingjia
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("session_key")
    private String sessionKey;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("session_secret")
    private String sessionSecret;

    @JsonProperty("error_description")
    private String errorDescription;

    @JsonProperty("error")
    private String error;

}