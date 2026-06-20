package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KamereonVehicleContract(
    @JsonProperty("type") String type,
    @JsonProperty("contractId") String contractId,
    @JsonProperty("code") String code,
    @JsonProperty("group") String group,
    @JsonProperty("durationMonths") Integer durationMonths,
    @JsonProperty("startDate") String startDate,
    @JsonProperty("endDate") String endDate,
    @JsonProperty("status") String status,
    @JsonProperty("statusLabel") String statusLabel,
    @JsonProperty("description") String description
) {}
