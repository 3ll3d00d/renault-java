package com.renault.api.exception;

/// Thrown when the API rejects a request due to invalid parameters.
public class InvalidInputException extends KamereonException {
    public InvalidInputException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
