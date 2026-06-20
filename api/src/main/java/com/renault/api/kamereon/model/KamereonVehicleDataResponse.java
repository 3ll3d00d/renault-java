package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.api.exception.RenaultException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KamereonVehicleDataResponse extends KamereonResponse {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty("data")
    private KamereonVehicleData data;

    public KamereonVehicleData getData() { return data; }

    public JsonNode getRawAttributes() {
        return data != null ? data.attributes() : MAPPER.nullNode();
    }

    public <T> T getAttributes(Class<T> type) {
        JsonNode attrs = getRawAttributes();
        if (attrs == null || attrs.isNull()) {
            try {
                return MAPPER.convertValue(MAPPER.createObjectNode(), type);
            } catch (Exception e) {
                throw new RenaultException("Failed to create empty attributes: " + e.getMessage(), e);
            }
        }
        try {
            return MAPPER.treeToValue(attrs, type);
        } catch (Exception e) {
            throw new RenaultException("Failed to parse attributes as %s: %s".formatted(type.getSimpleName(), e.getMessage()), e);
        }
    }
}
