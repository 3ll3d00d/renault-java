package com.renault.api.exception;

public class NotSupportedException extends KamereonException {
    public NotSupportedException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
