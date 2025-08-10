package com.chisom.authservice.exception;


import org.springframework.http.HttpStatus;

import java.io.Serial;

public class AccessTokenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2095211130465510979L;
    private  HttpStatus status;

    public AccessTokenException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }
}