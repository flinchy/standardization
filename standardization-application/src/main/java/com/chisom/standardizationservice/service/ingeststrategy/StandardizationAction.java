package com.chisom.standardizationservice.service.ingeststrategy;

import com.chisom.standardizationservice.domain.MessageType;

public interface StandardizationAction<T> {

    void ingest(MessageType type, T payload);
}
