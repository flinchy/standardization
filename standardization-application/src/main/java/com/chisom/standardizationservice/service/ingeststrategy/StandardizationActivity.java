package com.chisom.standardizationservice.service.ingeststrategy;

import com.chisom.standardizationservice.domain.MessageType;
import org.springframework.stereotype.Component;

@Component
public class StandardizationActivity<T> {

    /**
     * Process the message using the provided adapter.
     * This method doesn't store any state in the singleton bean,
     * making it thread-safe.
     *
     * @param adapter The adapter to use for processing
     * @param type The message type
     * @param payload The message payload
     */
    public void ingest(StandardizationAction<T> adapter, MessageType type, T payload) {
        adapter.ingest(type, payload);
    }
}

