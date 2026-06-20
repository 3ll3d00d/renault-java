package com.renault.api.exception;

/// Thrown when the vehicle's privacy mode is enabled, blocking location or data access.
public class PrivacyModeOnException extends KamereonException {
    public PrivacyModeOnException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
