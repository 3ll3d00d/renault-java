package com.renault.api.exception;

/// Thrown when an API call is made without a valid login token, or when the token has expired.
public class NotAuthenticatedException extends RenaultException {
    public NotAuthenticatedException(String message) { super(message); }
}
