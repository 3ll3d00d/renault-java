package com.renault.api.exception;

public class QuotaLimitException extends KamereonException {
    public QuotaLimitException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
