package com.renault.api.exception;

/// Thrown when Kamereon accepts the request but fails to forward it to the vehicle.
public class FailedForwardException extends KamereonException {
    public FailedForwardException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
