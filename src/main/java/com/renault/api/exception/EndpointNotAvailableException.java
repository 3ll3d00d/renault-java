package com.renault.api.exception;

public class EndpointNotAvailableException extends RenaultException {
    public EndpointNotAvailableException(String endpoint, String modelCode) {
        super("Endpoint '%s' is not available for model '%s'".formatted(endpoint, modelCode));
    }
}
