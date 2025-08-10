package com.chisom.standardizationservice.providers.beta;

import com.chisom.standardizationservice.domain.BetOutcome1X2;
import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.domain.StandardBetSettlement;
import com.chisom.standardizationservice.domain.StandardOddsChange;
import com.chisom.standardizationservice.providers.beta.dto.BetaMsg;
import com.chisom.standardizationservice.providers.beta.dto.BetaOddsChange;
import com.chisom.standardizationservice.providers.beta.dto.BetaSettlement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static com.chisom.standardizationservice.domain.BetOutcome1X2.getBetOutcome;
import static com.chisom.standardizationservice.domain.MessageType.BET_SETTLEMENT;
import static com.chisom.standardizationservice.domain.MessageType.ODDS_CHANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BetaIngestAdapterTest {

    @InjectMocks
    private BetaIngestAdapter adapter;

    @Test
    void oddsChangeTypeTest() {
        MessageType type = adapter.type(oddsChangeRequest());
        assertEquals(ODDS_CHANGE, type);
    }

    @Test
    void oddsSettlementTypeTest() {
        MessageType type = adapter.type(settlementRequest());
        assertEquals(BET_SETTLEMENT, type);
    }

    @Test
    void toOddsChangeTest() {
        StandardOddsChange oddsChange = adapter.toOddsChange(oddsChangeRequest());
        assertEquals(3, oddsChange.odds().size());
        assertEquals("ev456", oddsChange.eventId());
        assertEquals(0, oddsChange.odds().get(getBetOutcome("home")).compareTo(new BigDecimal("1.95")));
        assertEquals(0, oddsChange.odds().get(getBetOutcome("draw")).compareTo(new BigDecimal("3.2")));
        assertEquals(0, oddsChange.odds().get(getBetOutcome("away")).compareTo(new BigDecimal("4.0")));
    }

    @Test
    void toSettlementTest() {
        StandardBetSettlement settlement = adapter.toSettlement(settlementRequest());
        assertEquals(BetOutcome1X2.TWO, settlement.result());
        assertEquals("ev123", settlement.eventId());
    }

    private BetaMsg oddsChangeRequest() {
        Map<String, BigDecimal> odds = Map.of(
                "home", new BigDecimal("1.95"),
                "draw", new BigDecimal("3.2"),
                "away", new BigDecimal("4.0"));
        return new BetaOddsChange(
                "ODDS", "ev456", odds);
    }

    private BetaMsg settlementRequest() {
        return new BetaSettlement(
                "SETTLEMENT", "ev123", "away");
    }
}
