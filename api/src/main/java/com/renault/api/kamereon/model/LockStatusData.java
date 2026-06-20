package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LockStatusData(
    @JsonProperty("lockStatus") String lockStatus,
    @JsonProperty("doorStatusRearLeft") String doorStatusRearLeft,
    @JsonProperty("doorStatusRearRight") String doorStatusRearRight,
    @JsonProperty("doorStatusDriver") String doorStatusDriver,
    @JsonProperty("doorStatusPassenger") String doorStatusPassenger,
    @JsonProperty("hatchStatus") String hatchStatus,
    @JsonProperty("lastUpdateTime") String lastUpdateTime
) {}
