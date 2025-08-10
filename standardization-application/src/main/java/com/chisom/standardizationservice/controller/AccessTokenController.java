package com.chisom.standardizationservice.controller;

import com.chisom.authservice.dto.OAuth2AccessTokenRequest;
import com.chisom.authservice.dto.OAuth2AccessTokenResponse;
import com.chisom.authservice.service.OAuth2AccessTokenService;
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
public class AccessTokenController {

    private final OAuth2AccessTokenService tokenService;

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OAuth2AccessTokenResponse> ingest(
            @RequestBody @Valid OAuth2AccessTokenRequest request)  {
        return new ResponseEntity<>(tokenService.generateJWT(request), HttpStatus.OK);
    }
}
