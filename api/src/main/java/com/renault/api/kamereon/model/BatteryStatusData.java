package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BatteryStatusData(
    @JsonProperty("timestamp") String timestamp,
    @JsonProperty("batteryLevel") Integer batteryLevel,
    @JsonProperty("batteryTemperature") Integer batteryTemperature,
    @JsonProperty("batteryAutonomy") Integer batteryAutonomy,
    @JsonProperty("batteryCapacity") Integer batteryCapacity,
    @JsonProperty("batteryAvailableEnergy") Integer batteryAvailableEnergy,
    @JsonProperty("plugStatus") Integer plugStatus,
    @JsonProperty("chargingStatus") Double chargingStatus,
    @JsonProperty("chargingRemainingTime") Integer chargingRemainingTime,
    @JsonProperty("chargingInstantaneousPower") Double chargingInstantaneousPower,
    @JsonProperty("chargingRemainingTimeLastUpdateDateTime") String chargingRemainingTimeLastUpdateDateTime,
    @JsonProperty("V2L_SystemStatusDisplay") Integer v2lSystemStatusDisplay
) {}
