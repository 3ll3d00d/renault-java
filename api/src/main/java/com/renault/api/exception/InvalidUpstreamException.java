package com.renault.api.exception;

/// Thrown when Kamereon returns an unexpected or malformed response from an upstream service.
public class InvalidUpstreamException extends KamereonException {
    public InvalidUpstreamException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
