package com.renault.api.exception;

public class RenaultException extends RuntimeException {
    public RenaultException(String message) { super(message); }
    public RenaultException(String message, Throwable cause) { super(message, cause); }
}
