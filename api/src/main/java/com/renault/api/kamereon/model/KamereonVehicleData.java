package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KamereonVehicleData(
    @JsonProperty("type") String type,
    @JsonProperty("id") String id,
    @JsonProperty("attributes") JsonNode attributes
) {}
