package com.renault.api.exception;

/// Thrown when a vehicle method is called but the endpoint is not supported by this vehicle model.
public class EndpointNotAvailableException extends RenaultException {
    public EndpointNotAvailableException(String endpoint, String modelCode) {
        super("Endpoint '%s' is not available for model '%s'".formatted(endpoint, modelCode));
    }
}
