package com.renault.api.exception;

public class InvalidCredentialsException extends GigyaException {
    public InvalidCredentialsException(int errorCode, String errorDetails) {
        super(errorCode, errorDetails);
    }
}
