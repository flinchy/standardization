package com.chisom.standardizationservice.domain;

import lombok.Getter;

public enum ErrorCode {
    MISSING_TOKEN(""),
    TOKEN_EXPIRED(""),
    INVALID_TOKEN(""),
    PROVIDED_INPUT_INVALID("");

    @Getter
    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }
}
