package com.chisom.standardizationservice.providers.alpha.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "msg_type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AlphaOddsChange.class, name = "odds_update"),
        @JsonSubTypes.Type(value = AlphaSettlement.class, name = "settlement")
})
public sealed interface AlphaMsg permits AlphaOddsChange, AlphaSettlement {
    String msgType();
}
