package com.renault.api.exception;

/// Thrown when the account does not have permission to access the requested vehicle or resource.
public class AccessDeniedException extends KamereonException {
    public AccessDeniedException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
