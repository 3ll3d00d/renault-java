package com.renault.api.exception;

/// Thrown when the vehicle or account does not support the requested feature.
public class NotSupportedException extends KamereonException {
    public NotSupportedException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
