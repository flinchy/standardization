package com.chisom.standardizationservice.exception;

import com.chisom.standardizationservice.domain.ApiError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.math.BigDecimal;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Log4j2
@RestControllerAdvice
public class StandardizationExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ROOT_CAUSE = ". Root cause : ";
    private static final String GENERAL_FAILURE_ERROR_MESSAGE = "Oops! it looks like we are having trouble processing" +
            " your request. Please refresh and try again. If the problem persists, our support team is ready to" +
            " help you if needed.";

    /* ---------------------------------------------------------------------- */
    /* Spring overrides                                                       */
    /* ---------------------------------------------------------------------- */

    @Override
    public ResponseEntity<Object> handleMissingPathVariable(@NonNull MissingPathVariableException ex,
                                                            @NonNull HttpHeaders headers,
                                                            @NonNull HttpStatusCode status,
                                                            @NonNull WebRequest request) {
        String msg = "Path variable '" + ex.getVariableName() + "' is required";
        return handleExceptionInternal(ex, apiError(msg, ex, request), headers, BAD_REQUEST, request);
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String msg = "Request parameter '" + ex.getParameterName() + "' is required";
        return handleExceptionInternal(ex, apiError(msg, ex, request), headers, BAD_REQUEST, request);
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                               @NonNull HttpHeaders headers,
                                                               @NonNull HttpStatusCode status,
                                                               @NonNull WebRequest request) {

        Throwable cause = ex.getCause();
        String clientMsg = null;

        if (cause instanceof InvalidFormatException ife) {
            clientMsg = buildInvalidFormatMessage(ife);
        } else if (cause instanceof JsonParseException jpe) {
            clientMsg = buildJsonParseMessage(jpe);
        } else if (cause instanceof MismatchedInputException mie) {
            clientMsg = buildMismatchedInputMessage(mie);
        } else if (cause instanceof JsonMappingException jme) {
            clientMsg = buildJsonMappingMessage(ex, jme);
        }

        if (StringUtils.isBlank(clientMsg)) {
            clientMsg = firstLine(ex.getMessage());
            if (isNotBlank(clientMsg) && clientMsg.startsWith("Required request body is missing")) {
                clientMsg = "Required request body is missing";
            }
        }

        return handleExceptionInternal(ex, apiError(clientMsg, ex, request), headers, BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError ?
                    fieldError.getField() :
                    error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return handleExceptionInternal(ex, errors, headers, status, request);
    }

    @ExceptionHandler(StandardizationException.class)
    public ResponseEntity<ApiError> handleStandardizationException(Exception ex, WebRequest request) {
        logger.error(ex.getMessage(), ex);
        String path = (request instanceof ServletWebRequest swr) ? swr.getRequest().getRequestURI() : "N/A";

        ApiError body = ApiError.builder()
                .message(ex.getMessage())
                .path(path)
                .build();

        return ResponseEntity.internalServerError().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, WebRequest request) {
        logger.error("Unexpected error", ex);
        String path = (request instanceof ServletWebRequest swr) ? swr.getRequest().getRequestURI() : "N/A";

        ApiError body = ApiError.builder()
                .message("Error processing request")
                .path(path)
                .build();

        return ResponseEntity.internalServerError().body(body);
    }


    /* ---------------------------------------------------------------------- */
    /* Helpers: messages & ApiError                                           */
    /* ---------------------------------------------------------------------- */

    private ApiError apiError(String clientMessage, Exception ex, WebRequest request) {
        String developerMessage = ex.getMessage() + ROOT_CAUSE + ExceptionUtils.getRootCauseMessage(ex);
        logger.error(developerMessage, ex);

        clientMessage = overrideWithBusinessMessageIfPresent(clientMessage, ex);
        String path = (request instanceof ServletWebRequest swr)
                ? swr.getRequest().getRequestURI() : "N/A";

        return ApiError.builder()
                .message(StringUtils.defaultIfBlank(clientMessage, GENERAL_FAILURE_ERROR_MESSAGE))
                .path(path)
                .build();
    }

    private String overrideWithBusinessMessageIfPresent(String originalMessage, Exception ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof StandardizationException bre && isNotBlank(bre.getMessage())) {
            return bre.getMessage();
        }
        return originalMessage;
    }

    /* ---------------------------------------------------------------------- */
    /* Jackson-specific builders                                              */
    /* ---------------------------------------------------------------------- */

    private static String buildInvalidFormatMessage(InvalidFormatException ex) {
        String field = firstFieldName(ex.getPath()).orElse(null);
        String base = "Invalid value";
        if (field != null) base += " for field '" + field + "'";

        String typeHint = dataTypeHint(ex.getTargetType()).orElse(null);
        if (typeHint != null) base += ", " + typeHint;

        return base;
    }

    private static String buildJsonParseMessage(JsonParseException ex) {
        // JsonParseException doesn't always carry field info. Use first line only.
        return "Invalid request body: " + firstLine(ex.getOriginalMessage());
    }

    private static String buildMismatchedInputMessage(MismatchedInputException ex) {
        String field = fieldName(ex).orElse(null);
        return (field == null)
                ? "Invalid request body"
                : "Invalid value for field '" + field + "'";
    }

    private static String buildJsonMappingMessage(HttpMessageNotReadableException wrapper,
                                                  JsonMappingException ex) {
        String field = firstFieldName(ex.getPath()).orElse(null);
        Throwable root = rootCause(ex);
        String detail = firstLine(Optional.ofNullable(root).map(Throwable::getMessage).orElse(null));

        StringBuilder sb = new StringBuilder("Invalid value");
        if (field != null) sb.append(" for field '").append(field).append("'");
        if (isNotBlank(detail)) sb.append(": ").append(detail);
        else sb.append(": ").append(firstLine(wrapper.getMessage()));

        return sb.toString();
    }

    /* ---------------------------------------------------------------------- */
    /* Small utility methods                                                  */
    /* ---------------------------------------------------------------------- */

    private static Optional<String> dataTypeHint(Class<?> type) {
        if (type == null) return Optional.empty();
        if (type == Boolean.class || type == boolean.class)
            return Optional.of("it must be boolean (true/false)");
        if (type == Integer.class || type == int.class || type == Long.class || type == long.class)
            return Optional.of("it must be a non-decimal number");
        if (type == Double.class || type == double.class
                || type == Float.class || type == float.class
                || type == BigDecimal.class)
            return Optional.of("it must be a decimal number");
        return Optional.empty();
    }

    private static Optional<String> firstFieldName(List<JsonMappingException.Reference> path) {
        return path == null ? Optional.empty()
                : path.stream()
                .map(JsonMappingException.Reference::getFieldName)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .findFirst();
    }

    private static Optional<String> fieldName(MismatchedInputException ex) {
        if (ex instanceof InvalidTypeIdException itie) {
            return Optional.ofNullable(itie.getTypeId());
        }
        try {
            var last = ex.getPath().isEmpty() ? null : ex.getPath().getLast();
            if (last != null && isNotBlank(last.getFieldName())) {
                return Optional.of(last.getFieldName());
            }
            var first = ex.getPath().isEmpty() ? null : ex.getPath().getFirst();
            return first == null ? Optional.empty() : Optional.ofNullable(first.getFieldName());
        } catch (Exception ignore) {
            return Optional.empty();
        }
    }

    private static Throwable rootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        Throwable rootCause = ExceptionUtils.getRootCause(throwable);
        return rootCause != null ? rootCause : throwable;
    }

    private static String firstLine(String s) {
        if (s == null) return null;
        return s.split("\\R", 2)[0];
    }
}
