package com.renault.api.exception;

public class FailedForwardException extends KamereonException {
    public FailedForwardException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
