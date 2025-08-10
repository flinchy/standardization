package com.chisom.standardizationservice.providers.alpha;

import com.chisom.standardizationservice.providers.alpha.dto.AlphaMsg;

public interface AlphaIngestUseCase {

    void ingest(AlphaMsg msg);
}
