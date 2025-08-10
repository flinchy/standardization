package com.chisom.standardizationservice.providers.alpha.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public record AlphaSettlement(
        @JsonProperty("msg_type")
        @SerializedName("msg_type")
        String msgType,
        @JsonProperty("event_id")
        @SerializedName("event_id")
        String eventId,
        String outcome
) implements AlphaMsg {
}
