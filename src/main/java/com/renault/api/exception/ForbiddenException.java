package com.renault.api.exception;

public class ForbiddenException extends KamereonException {
    public ForbiddenException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
