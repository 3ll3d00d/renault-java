package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HvacStatusData(
    @JsonProperty("lastUpdateTime") String lastUpdateTime,
    @JsonProperty("externalTemperature") Double externalTemperature,
    @JsonProperty("internalTemperature") Double internalTemperature,
    @JsonProperty("hvacStatus") String hvacStatus,
    @JsonProperty("nextHvacStartDate") String nextHvacStartDate,
    @JsonProperty("socThreshold") Double socThreshold
) {}
