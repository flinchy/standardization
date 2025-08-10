package com.chisom.standardizationservice.providers.alpha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Map;

public record AlphaOddsChange(
        @JsonProperty("msg_type")
        @SerializedName("msg_type")
        String msgType,
        @JsonProperty("event_id")
        @SerializedName("event_id")
        String eventId,
        Map<String, BigDecimal> values
) implements AlphaMsg {

}
