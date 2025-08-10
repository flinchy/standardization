package com.chisom.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public record OAuth2AccessTokenRequest(
        @SerializedName("grant_Type")
        @JsonProperty("grant_Type")
        String grantType,
        @SerializedName("client_id")
        @JsonProperty("client_id")
        String clientId,
        @SerializedName("client_secret")
        @JsonProperty("client_secret")
        String clientSecret,
        String audience
) {
}
