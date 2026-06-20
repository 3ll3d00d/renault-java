package com.renault.api.exception;

/// Thrown when the requested vehicle or Kamereon resource does not exist.
public class ResourceNotFoundException extends KamereonException {
    public ResourceNotFoundException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
