package com.chisom.standardizationservice.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@ToString
public class StandardizationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8710739658771517719L;
    /**
     * Represents additional metadata relevant to the exception.
     * This field provides contextual information that may assist
     * in understanding or debugging the error.
     * <p>
     * The metadata can be used to include supplementary details
     * about the exception which are represented as a string value.
     */
    @JsonProperty("metadata")
    @SerializedName("metadata")
    private final String metadata;

    /**
     * Represents a URL link providing additional information about the error.
     * This field can be used to point to documentation, troubleshooting steps,
     * or other resources that can help the user understand the exception context.
     */
    @JsonProperty("infoLink")
    @SerializedName("infoLink")
    private final String infoLink;

    /**
     * Represents an application-specific error code used for categorizing and identifying
     * the type of error that occurred. This field is intended to provide concise
     * and consistent information about the error, making it easier to debug, log, and handle
     * within the Buy recipe application.
     * <p>
     * The error code is a string and serves as a unique identifier for particular error scenarios.
     */
    @JsonProperty("errorCode")
    @SerializedName("errorCode")
    private final String errorCode;

    /**
     * Represents the HTTP status code that categorizes the nature of the exception.
     * This field provides a numerical value and descriptive status to indicate
     * the type of error that occurred as per HTTP standards.
     * <p>
     * The status can be used to accurately represent the error context in both
     * debugging and client-server communication scenarios.
     */
    @JsonProperty("status")
    @SerializedName("status")
    protected final HttpStatus status;

    @Builder
    public StandardizationException(String message, String metadata, String infoLink, String errorCode, HttpStatus status) {
        super(message);
        this.metadata = metadata;
        this.infoLink = infoLink;
        this.errorCode = errorCode;
        this.status = status == null ? INTERNAL_SERVER_ERROR : status;
    }
}
