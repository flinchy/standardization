package com.chisom.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public record OAuth2AccessTokenResponse(
        @SerializedName("access_token")
        @JsonProperty("access_token")
        String access_token,
        String scope,
        @SerializedName("expires_in")
        @JsonProperty("expires_in")
        Long expiresIn,
        @SerializedName("token_type")
        @JsonProperty("token_type")
        String token_type
) {
}
