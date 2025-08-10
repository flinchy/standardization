package com.chisom.standardizationservice.domain;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record StandardBetSettlement(String eventId,
                                    Market market,
                                    BetOutcome1X2 result,
                                    LocalDateTime dateReceived
) {
}