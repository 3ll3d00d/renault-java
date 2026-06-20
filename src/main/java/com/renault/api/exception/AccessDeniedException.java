package com.renault.api.exception;

public class AccessDeniedException extends KamereonException {
    public AccessDeniedException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
