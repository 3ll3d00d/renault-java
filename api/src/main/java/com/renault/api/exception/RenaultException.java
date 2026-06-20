package com.renault.api.exception;

/// Base unchecked exception for all errors thrown by this library.
public class RenaultException extends RuntimeException {
    public RenaultException(String message) { super(message); }
    public RenaultException(String message, Throwable cause) { super(message, cause); }
}
