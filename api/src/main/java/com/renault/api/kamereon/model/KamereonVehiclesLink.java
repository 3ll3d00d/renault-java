package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KamereonVehiclesLink(
    @JsonProperty("vin") String vin,
    @JsonProperty("vehicleDetails") KamereonVehicleDetails vehicleDetails
) {}
