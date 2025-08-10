package com.chisom.standardizationservice.providers.beta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Map;

public record BetaOddsChange(String type,
                             @JsonProperty("event_id")
                             @SerializedName("event_id")
                             String eventId,
                             Map<String, BigDecimal> odds) implements BetaMsg {
}
