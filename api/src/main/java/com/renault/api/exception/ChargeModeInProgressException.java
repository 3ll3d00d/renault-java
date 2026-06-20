package com.renault.api.exception;

/// Thrown when a charge action is rejected because a charge mode change is already in progress.
public class ChargeModeInProgressException extends KamereonException {
    public ChargeModeInProgressException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
