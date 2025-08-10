package com.chisom.standardizationservice.providers.alpha;

import com.chisom.standardizationservice.domain.BetOutcome1X2;
import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.domain.StandardBetSettlement;
import com.chisom.standardizationservice.domain.StandardOddsChange;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaMsg;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaOddsChange;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaSettlement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static com.chisom.standardizationservice.domain.MessageType.BET_SETTLEMENT;
import static com.chisom.standardizationservice.domain.MessageType.ODDS_CHANGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AlphaIngestAdapterTest {

    @InjectMocks
    private AlphaIngestAdapter adapter;

    @Test
    void oddsChangeTypeTest() {
        MessageType type = adapter.type(oddsChangeRequest());
        assertEquals(ODDS_CHANGE, type);
    }

    @Test
    void settlementTypeTest() {
        MessageType type = adapter.type(settlementRequest());
        assertEquals(BET_SETTLEMENT, type);
    }

    @Test
    void toOddsChangeTest() {
        StandardOddsChange oddsChange = adapter.toOddsChange(oddsChangeRequest());
        assertEquals(3, oddsChange.odds().size());
        assertEquals("ev123", oddsChange.eventId());
        assertEquals(0, oddsChange.odds().get(BetOutcome1X2.ONE).compareTo(new BigDecimal("2.0")));
        assertEquals(0, oddsChange.odds().get(BetOutcome1X2.X).compareTo(new BigDecimal("3.1")));
        assertEquals(0, oddsChange.odds().get(BetOutcome1X2.TWO).compareTo(new BigDecimal("3.8")));
    }

    @Test
    void toSettlementTest() {
        StandardBetSettlement settlement = adapter.toSettlement(settlementRequest());
        assertEquals(BetOutcome1X2.ONE, settlement.result());
        assertEquals("ev123", settlement.eventId());
    }

    private AlphaMsg oddsChangeRequest() {
        Map<String, BigDecimal> values = Map.of(
                "1", new BigDecimal("2.0"),
                "x", new BigDecimal("3.1"),
                "2", new BigDecimal("3.8"));
        return new AlphaOddsChange(
                "odds_update", "ev123", values);
    }

    private AlphaMsg settlementRequest() {
        return new AlphaSettlement(
                "settlement", "ev123", "1");
    }
}
