package com.renault.api.exception;

public class InvalidInputException extends KamereonException {
    public InvalidInputException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
