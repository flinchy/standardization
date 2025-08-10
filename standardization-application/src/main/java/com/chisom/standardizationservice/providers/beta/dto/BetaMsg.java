package com.chisom.standardizationservice.providers.beta.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BetaOddsChange.class, name = "ODDS"),
        @JsonSubTypes.Type(value = BetaSettlement.class, name = "SETTLEMENT")
})
public sealed interface BetaMsg permits BetaOddsChange, BetaSettlement {
    String type();
}
