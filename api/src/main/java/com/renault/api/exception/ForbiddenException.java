package com.renault.api.exception;

/// Thrown when the requested action is forbidden (e.g. account-level restrictions).
public class ForbiddenException extends KamereonException {
    public ForbiddenException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
