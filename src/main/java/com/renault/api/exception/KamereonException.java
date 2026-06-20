package com.renault.api.exception;

public class KamereonException extends RenaultException {
    private final String errorCode;
    private final String errorDetails;

    public KamereonException(String errorCode, String errorDetails) {
        super("Kamereon error %s: %s".formatted(errorCode, errorDetails));
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }

    public String getErrorCode() { return errorCode; }
    public String getErrorDetails() { return errorDetails; }
}
