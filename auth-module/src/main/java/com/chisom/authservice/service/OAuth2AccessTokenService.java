package com.chisom.authservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.chisom.authservice.config.AuthProperties;
import com.chisom.authservice.dto.OAuth2AccessTokenRequest;
import com.chisom.authservice.dto.OAuth2AccessTokenResponse;
import com.chisom.authservice.exception.AccessTokenException;
import com.chisom.commons.util.CryptoUtil;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public record OAuth2AccessTokenService(AuthProperties properties) {

    public OAuth2AccessTokenResponse generateJWT(OAuth2AccessTokenRequest request) {

        validateClientSecret(request);
        String jwt;
        LocalDateTime issuedAt = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime expiresAt = issuedAt.plusYears(60);
        long expirationInSeconds = 60L * 365 * 24 * 60 * 60; // 60 years in seconds (1,892,160,000)  This is just for testing purpose, Usually it will be short-lived
        String subject = UUID.randomUUID().toString();  //This is just for testing purpose.
        String scope = "internal-operation";

        try {
            Algorithm algorithm = Algorithm.HMAC256(CryptoUtil.aesDecrypt(properties.getSecret(),
                    properties.getAesKey()));

            jwt = JWT.create()
                    .withIssuer("https://login-dev.com")  //this would be a real auth issuer for a production use case
                    .withSubject(UUID.randomUUID().toString())
                    .withClaim("azp", subject)
                    .withAudience("https://standardization-service.com")
                    .withKeyId(properties.getKeyId()) //generated with the public key
                    .withJWTId(UUID.randomUUID().toString())
                    .withClaim("gty", "client_credentials")
                    .withIssuedAt(issuedAt.atZone(ZoneId.systemDefault()).toInstant())
                    .withNotBefore(issuedAt.atZone(ZoneId.systemDefault()).toInstant())
                    .withExpiresAt(expiresAt.atZone(ZoneId.systemDefault()).toInstant())
                    .withClaim("scope", scope)
                    .sign(algorithm);
        } catch (Exception e) {
            String msg = "Could not obtain access token, please try again later or contact support";
            throw new AccessTokenException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new OAuth2AccessTokenResponse(
                jwt, scope, expirationInSeconds, "Bearer"
        );
    }

    private void validateClientSecret(OAuth2AccessTokenRequest request) {
        if (!properties.getClientSecret().equals(request.clientSecret()) &&
                !properties.getClientId().equals(request.clientId())) {
            String msg = "Invalid client credentials";
            throw new AccessTokenException(msg, HttpStatus.UNAUTHORIZED);
        }
    }
}
