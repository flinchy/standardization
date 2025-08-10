package com.chisom.standardizationservice.providers.beta;

import com.chisom.standardizationservice.adapters.AbstractIngestAdapter;
import com.chisom.standardizationservice.domain.BetOutcome1X2;
import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.domain.StandardBetSettlement;
import com.chisom.standardizationservice.domain.StandardOddsChange;
import com.chisom.standardizationservice.exception.StandardizationException;
import com.chisom.standardizationservice.providers.beta.dto.BetaMsg;
import com.chisom.standardizationservice.providers.beta.dto.BetaOddsChange;
import com.chisom.standardizationservice.providers.beta.dto.BetaSettlement;
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
public class BetaIngestAdapter extends AbstractIngestAdapter<BetaMsg> {

    public BetaIngestAdapter(StandardizationPublisher publisher) {
        super(publisher);
    }

    public MessageType type(BetaMsg payload) {
        String type = payload.type();
        return switch (type) {
            case "ODDS" -> MessageType.ODDS_CHANGE;
            case "SETTLEMENT" -> MessageType.BET_SETTLEMENT;
            default -> throw new IllegalArgumentException("Unknown msg_type: %s".formatted(type));
        };
    }

    protected StandardOddsChange toOddsChange(BetaMsg payload) {
        logger.info("Beta odds change message");
        BetaOddsChange oddsChange = (BetaOddsChange) payload;

        Map<BetOutcome1X2, BigDecimal> odds = new EnumMap<>(BetOutcome1X2.class);

        try {
            oddsChange.odds().forEach((key, value) ->
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

    protected StandardBetSettlement toSettlement(BetaMsg payload) {
        logger.info("Beta settlement message");
        BetaSettlement settlement = (BetaSettlement) payload;

        return StandardBetSettlement.builder()
                .market(ONE_X_TWO)
                .result(BetOutcome1X2.getBetOutcome(settlement.result()))
                .eventId(settlement.eventId())
                .dateReceived(LocalDateTime.now())
                .build();
    }
}
