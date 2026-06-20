package com.renault.api.exception;

public class ChargeModeInProgressException extends KamereonException {
    public ChargeModeInProgressException(String errorCode, String errorMessage) { super(errorCode, errorMessage); }
}
