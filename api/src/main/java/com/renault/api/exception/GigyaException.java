package com.renault.api.exception;

/// Error from the Gigya authentication layer. {@link #getErrorCode()} returns a Gigya
/// numeric error code (e.g. `403005` = token expired, `403013` = unauthorized).
/// When no structured code is available (e.g. a network or parse failure), `errorCode`
/// is `-1`.
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
