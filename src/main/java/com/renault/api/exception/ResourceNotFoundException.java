package com.renault.api.exception;

public class ResourceNotFoundException extends KamereonException {
    public ResourceNotFoundException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
