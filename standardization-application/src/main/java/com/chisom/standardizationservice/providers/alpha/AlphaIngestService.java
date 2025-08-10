package com.chisom.standardizationservice.providers.alpha;

import com.chisom.standardizationservice.domain.MessageType;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaMsg;
import com.chisom.standardizationservice.service.ingeststrategy.StandardizationActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlphaIngestService implements AlphaIngestUseCase {

    private final AlphaIngestAdapter adapter;
    private final StandardizationActivity<AlphaMsg> activity;

    public void ingest(AlphaMsg msg) {
        MessageType type = adapter.type(msg);
        activity.useAdapter(adapter).ingest(type, msg);
    }

}
