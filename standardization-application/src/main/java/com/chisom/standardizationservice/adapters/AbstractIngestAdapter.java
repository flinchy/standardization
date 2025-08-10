package com.chisom.standardizationservice.adapters;

import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.domain.StandardBetSettlement;
import com.chisom.standardizationservice.domain.StandardOddsChange;
import com.chisom.standardizationservice.service.StandardizationPublisher;
import com.chisom.standardizationservice.service.ingeststrategy.StandardizationAction;

public abstract class AbstractIngestAdapter<T> implements StandardizationAction<T> {

    protected final StandardizationPublisher publisher;

    protected AbstractIngestAdapter(StandardizationPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void ingest(MessageType type, T msg) {
        switch (type) {
            case ODDS_CHANGE:
                publisher.send(toOddsChange(msg));
                break;
            case BET_SETTLEMENT:
                publisher.send(toSettlement(msg));
                break;
            default:
                throw new IllegalArgumentException("Unknown msgType: %s".formatted(type));
        }
    }

    protected abstract StandardOddsChange toOddsChange(T payload);

    protected abstract StandardBetSettlement toSettlement(T payload);

    public abstract MessageType type(T payload);
}
