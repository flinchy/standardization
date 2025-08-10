package com.chisom.standardizationservice.domain;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record StandardOddsChange(String eventId,
                                 Market market,
                                 Map<BetOutcome1X2, BigDecimal> odds,
                                 LocalDateTime dateReceived) {
}
