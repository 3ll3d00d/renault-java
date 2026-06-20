package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CockpitData(
    @JsonProperty("totalMileage") Double totalMileage,
    @JsonProperty("fuelAutonomy") Double fuelAutonomy,
    @JsonProperty("fuelQuantity") Double fuelQuantity
) {}
