package com.chisom.standardizationservice.providers.beta;

import com.chisom.standardizationservice.providers.beta.dto.BetaMsg;

public interface BetaIngestUseCase {

    void ingest(BetaMsg msg);
}
