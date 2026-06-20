package com.renault.api.exception;

public class InvalidUpstreamException extends KamereonException {
    public InvalidUpstreamException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
