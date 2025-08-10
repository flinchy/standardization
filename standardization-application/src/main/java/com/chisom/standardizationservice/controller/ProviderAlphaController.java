package com.chisom.standardizationservice.controller;

import com.chisom.standardizationservice.providers.alpha.AlphaIngestUseCase;
import com.chisom.standardizationservice.providers.alpha.dto.AlphaMsg;
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
public class ProviderAlphaController {

    private final AlphaIngestUseCase ingestUseCase;

    @PostMapping(value = "/provider-alpha/feed", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> ingest(
            @RequestBody @Valid AlphaMsg request
    ) {
        ingestUseCase.ingest(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
