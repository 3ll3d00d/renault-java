package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/// Battery status snapshot. Field units:
///
/// - `batteryLevel` — state of charge, 0–100 %
/// - `batteryAutonomy` — estimated remaining range, km
/// - `batteryAvailableEnergy` — available energy, kWh
/// - `batteryTemperature` — °C
/// - `plugStatus` — 0 = unplugged, 1 = plugged in
/// - `chargingStatus` — 0.0 = not charging, 1.0 = charging, 2.0 = scheduled, −1.0 = error
/// - `chargingRemainingTime` — minutes until full
/// - `chargingInstantaneousPower` — kW (or W if {@link KamereonVehicleDetails#reportsChargingPowerInWatts()} is true)
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
