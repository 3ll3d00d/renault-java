package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KamereonVehicleDetailsGroup(
    @JsonProperty("code") String code,
    @JsonProperty("label") String label,
    @JsonProperty("group") String group
) {}
