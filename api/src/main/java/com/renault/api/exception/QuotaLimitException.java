package com.renault.api.exception;

/// Thrown when the Kamereon API rate limit has been exceeded. Back off and retry later.
public class QuotaLimitException extends KamereonException {
    public QuotaLimitException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
