package com.chisom.standardizationservice.controller;

import com.chisom.standardizationservice.providers.beta.BetaIngestUseCase;
import com.chisom.standardizationservice.providers.beta.dto.BetaMsg;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProviderBetaController {

    private final BetaIngestUseCase ingestUseCase;

    @PostMapping(value = "/provider-beta/feed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> ingest(
            @RequestBody @Valid BetaMsg request
    ) {
        ingestUseCase.ingest(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
