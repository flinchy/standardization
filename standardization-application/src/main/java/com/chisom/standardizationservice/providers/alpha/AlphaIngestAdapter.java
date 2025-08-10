package com.chisom.standardizationservice.providers.alpha;

import com.chisom.standardizationservice.adapters.AbstractIngestAdapter;
import com.chisom.standardizationservice.domain.BetOutcome1X2;
import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.domain.StandardBetSettlement;
import com.chisom.standardizationservice.domain.StandardOddsChange;
import com.chisom.standardizationservice.exception.StandardizationException;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaMsg;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaOddsChange;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaSettlement;
import com.chisom.standardizationservice.service.StandardizationPublisher;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

import static com.chisom.standardizationservice.domain.Market.ONE_X_TWO;

@Log4j2
@Component
public class AlphaIngestAdapter extends AbstractIngestAdapter<AlphaMsg> {

    public AlphaIngestAdapter(StandardizationPublisher publisher) {
        super(publisher);
    }

    public MessageType type(AlphaMsg payload) {
        String type = payload.msgType();
        return switch (type) {
            case "odds_update" -> MessageType.ODDS_CHANGE;
            case "settlement" -> MessageType.BET_SETTLEMENT;
            default -> throw new IllegalArgumentException("Unknown msg_type: %s".formatted(type));
        };
    }

    protected StandardOddsChange toOddsChange(AlphaMsg payload) {
        logger.info("Alpha odds change message");

        AlphaOddsChange oddsChange = (AlphaOddsChange) payload;

        Map<BetOutcome1X2, BigDecimal> odds = new EnumMap<>(BetOutcome1X2.class);

        try {
            oddsChange.values().forEach((key, value) ->
                    odds.put(BetOutcome1X2.getBetOutcome(key), value));
        } catch (Exception e) {
            logger.error(e);
            throw StandardizationException.builder()
                    .message("Failed to parse odds")
                    .build();
        }
        return StandardOddsChange.builder()
                .market(ONE_X_TWO)
                .odds(odds)
                .eventId(oddsChange.eventId())
                .dateReceived(LocalDateTime.now())
                .build();
    }

    protected StandardBetSettlement toSettlement(AlphaMsg payload) {
        logger.info("Alpha settlement message");
        AlphaSettlement settlement = (AlphaSettlement) payload;

        return StandardBetSettlement.builder()
                .market(ONE_X_TWO)
                .result(BetOutcome1X2.getBetOutcome(settlement.outcome()))
                .eventId(settlement.eventId())
                .dateReceived(LocalDateTime.now())
                .build();
    }
}