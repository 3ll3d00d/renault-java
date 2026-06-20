package com.renault.api.exception;

/// Thrown when login fails due to wrong username or password.
public class InvalidCredentialsException extends GigyaException {
    public InvalidCredentialsException(int errorCode, String errorDetails) {
        super(errorCode, errorDetails);
    }
}
