package com.chisom.standardizationservice.providers.beta;

import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.providers.beta.dto.BetaMsg;
import com.chisom.standardizationservice.service.ingeststrategy.StandardizationActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BetaIngestService implements BetaIngestUseCase {

    private final BetaIngestAdapter adapter;
    private final StandardizationActivity<BetaMsg> activity;

    @Override
    public void ingest(BetaMsg msg) {
        MessageType type = adapter.type(msg);
        activity.useAdapter(adapter).ingest(type, msg);
    }
}
