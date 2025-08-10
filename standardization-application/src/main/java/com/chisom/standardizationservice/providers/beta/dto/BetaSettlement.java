package com.chisom.standardizationservice.providers.beta.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public record BetaSettlement(String type,
                             @JsonProperty("event_id")
                             @SerializedName("event_id")
                             String eventId,
                             String result) implements BetaMsg {
}
