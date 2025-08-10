package com.chisom.standardizationservice.service.ingeststrategy;

import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.exception.StandardizationException;
import org.springframework.stereotype.Component;

@Component
public class StandardizationActivity<T> {

    private StandardizationAction<T> standardizationMediumContext;

    public StandardizationActivity<T> useAdapter(StandardizationAction<T> standardizationMediumContext) {
        this.standardizationMediumContext = standardizationMediumContext;
        return this;
    }

    public void ingest(MessageType type, T payload) {
        if (standardizationMediumContext == null) {
            throw StandardizationException.builder()
                    .message("StandardizationMediumContext is null")
                    .build();
        }
        standardizationMediumContext.ingest(type, payload);
    }
}

