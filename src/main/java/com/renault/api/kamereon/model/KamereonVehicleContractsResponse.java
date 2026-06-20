package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KamereonVehicleContractsResponse extends KamereonResponse {
    @JsonProperty("contractList")
    private List<KamereonVehicleContract> contractList;

    public List<KamereonVehicleContract> getContractList() { return contractList; }
}
