package com.chisom.standardizationservice.config.security;

import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chisom.standardizationservice.domain.ErrorCode;
import com.chisom.standardizationservice.domain.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static com.chisom.standardizationservice.domain.ErrorCode.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
public record LimitFlexJwtAuthenticationEntryPoint(ObjectMapper mapper) implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response, AuthenticationException e) throws IOException {

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorCode errorCode = determineErrorCode(e);
        String cause = determineCause(e);
        boolean expiredToken = isTokenExpired(e);
        boolean isInvalidToken = isTokenInvalid(e);

        logRequestInfo(request, cause);

        String token = extractToken(request);
        if (!expiredToken && !isInvalidToken && StringUtils.isNotBlank(token)) {
            expiredToken = validateTokenExpiration(token);
        }

        ApiError error = buildApiError(request, errorCode, expiredToken, cause, token);
        response.addHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        try (OutputStream out = response.getOutputStream()) {
            mapper.writeValue(out, error);
        }
    }

    @NotNull
    private static ApiError buildApiError(HttpServletRequest request,
                                          ErrorCode errorCode, boolean expiredToken, String cause, String token) {

        if (expiredToken) {
            return ApiError.create("The token has expired",
                    request.getRequestURI(), TOKEN_EXPIRED.name(), "", Collections.emptyList());
        }

        if (StringUtils.isBlank(token)) {
            return ApiError.create("Token is missing",
                    request.getRequestURI(), MISSING_TOKEN.name(), "", Collections.emptyList());
        }

        return ApiError.create(cause,
                request.getRequestURI(), errorCode != null ? errorCode.name() : INVALID_TOKEN.name(),
                "", Collections.emptyList());

    }

    public void logRequestInfo(HttpServletRequest request, String cause) {
        String clientIp = getClientIp(request);
        String requestUrl = getRequestUrl(request);
        String httpMethod = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");

        logger.info(String.format("Request Info: [Client IP: %s] [URL: %s] [Method: %s] " +
                        "[User-Agent: %s] [Referrer: %s] [Cause: %s]",
                clientIp, requestUrl, httpMethod, userAgent, referrer, cause), cause);
    }

    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    private String getRequestUrl(HttpServletRequest request) {
        StringBuilder requestUrl = new StringBuilder(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestUrl.append('?').append(queryString);
        }
        return requestUrl.toString();
    }

    private String determineCause(AuthenticationException e) {
        if (e instanceof InvalidBearerTokenException ibte) {
            if (ibte.getError() != null && ibte.getError().getDescription().contains("signature")) {
                return "The token provided is not valid";
            }
            if (ibte.getError() != null && ibte.getError().getErrorCode().contains("invalid_token")) {
                return "The token provided is not valid";
            }
            return e.getMessage();
        }

        if (e.getCause() instanceof TokenExpiredException) {
            return "The token has expired";
        }

        if (e instanceof BadCredentialsException ||
                e.getCause() instanceof SignatureVerificationException ||
                e.getCause() instanceof SigningKeyNotFoundException) {
            return "Invalid token";
        }

        return e.getMessage();
    }

    private ErrorCode determineErrorCode(AuthenticationException e) {
        if (e instanceof InvalidBearerTokenException ibte) {
            if (ibte.getError() != null && ibte.getError().getDescription().contains("signature")) {
                return INVALID_TOKEN;
            }
            if (ibte.getError() != null && ibte.getError().getErrorCode().contains("invalid_token")) {
                return INVALID_TOKEN;
            }
        }
        if (e.getCause() instanceof TokenExpiredException) {
            return TOKEN_EXPIRED;
        }
        if (e instanceof BadCredentialsException ||
                e.getCause() instanceof SignatureVerificationException ||
                e.getCause() instanceof SigningKeyNotFoundException) {
            return INVALID_TOKEN;
        }
        if (e instanceof InvalidBearerTokenException) {
            return MISSING_TOKEN;
        }
        return INVALID_TOKEN; // Default error code
    }

    private boolean isTokenExpired(AuthenticationException e) {
        return e.getCause() instanceof TokenExpiredException ||
                (e instanceof InvalidBearerTokenException && e.getMessage().contains("expire"));
    }

    private boolean isTokenInvalid(AuthenticationException e) {
        return e instanceof BadCredentialsException ||
                e.getCause() instanceof SignatureVerificationException ||
                e.getCause() instanceof SigningKeyNotFoundException;
    }

    private boolean validateTokenExpiration(String token) {
        try {
            DecodedJWT decodedJwt = JWT.decode(token.trim());
            if (decodedJwt.getExpiresAt() != null) {
                LocalDateTime expirationDate = toLocalDateTime(decodedJwt.getExpiresAt());
                return !expirationDate.isAfter(LocalDateTime.now());
            }
        } catch (Exception ex) {
            logger.error("Could not decode JWT. Reason: {}", ExceptionUtils.getRootCauseMessage(ex));
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private static LocalDateTime toLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
