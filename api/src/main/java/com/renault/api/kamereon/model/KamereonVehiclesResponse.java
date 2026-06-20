package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KamereonVehiclesResponse extends KamereonResponse {
    @JsonProperty("accountId") private String accountId;
    @JsonProperty("country") private String country;
    @JsonProperty("vehicleLinks") private List<KamereonVehiclesLink> vehicleLinks;

    public String getAccountId() { return accountId; }
    public String getCountry() { return country; }
    public List<KamereonVehiclesLink> getVehicleLinks() { return vehicleLinks; }
}
