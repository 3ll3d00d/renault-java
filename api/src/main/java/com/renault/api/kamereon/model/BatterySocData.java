package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BatterySocData(
    @JsonProperty("lastEnergyUpdateTimestamp") String lastEnergyUpdateTimestamp,
    @JsonProperty("socMin") Integer socMin,
    @JsonProperty("socTarget") Integer socTarget
) {}
