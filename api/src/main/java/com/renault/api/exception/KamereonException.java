package com.renault.api.exception;

/// Error from the Kamereon vehicle API. {@link #getErrorCode()} returns a Kamereon
/// string error code (e.g. `"err.func.403"`, `"err.tech.500"`).
/// Specific subclasses are thrown for well-known codes; catch this class to handle
/// any Kamereon error generically.
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
