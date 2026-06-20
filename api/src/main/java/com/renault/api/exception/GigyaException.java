package com.renault.api.exception;

public class GigyaException extends RenaultException {
    private final int errorCode;
    private final String errorDetails;

    public GigyaException(String message) {
        super(message);
        this.errorCode = -1;
        this.errorDetails = null;
    }

    public GigyaException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = -1;
        this.errorDetails = null;
    }

    public GigyaException(int errorCode, String errorDetails) {
        super("Gigya error %d: %s".formatted(errorCode, errorDetails));
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }

    public int getErrorCode() { return errorCode; }
    public String getErrorDetails() { return errorDetails; }
}
