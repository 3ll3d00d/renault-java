package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationData(
    @JsonProperty("lastUpdateTime") String lastUpdateTime,
    @JsonProperty("gpsLatitude") Double gpsLatitude,
    @JsonProperty("gpsLongitude") Double gpsLongitude,
    @JsonProperty("gpsDirection") Integer gpsDirection
) {}
