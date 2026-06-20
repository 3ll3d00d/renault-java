package com.renault.api.kamereon;

public record EndpointDefinition(String endpoint, String mode) {
    public EndpointDefinition(String endpoint) {
        this(endpoint, "default");
    }
}
