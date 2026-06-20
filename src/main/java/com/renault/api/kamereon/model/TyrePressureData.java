package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TyrePressureData(
    @JsonProperty("flPressure") Integer flPressure,
    @JsonProperty("frPressure") Integer frPressure,
    @JsonProperty("rlPressure") Integer rlPressure,
    @JsonProperty("rrPressure") Integer rrPressure,
    @JsonProperty("flStatus") Integer flStatus,
    @JsonProperty("frStatus") Integer frStatus,
    @JsonProperty("rlStatus") Integer rlStatus,
    @JsonProperty("rrStatus") Integer rrStatus,
    @JsonProperty("lastUpdateTime") String lastUpdateTime
) {}
