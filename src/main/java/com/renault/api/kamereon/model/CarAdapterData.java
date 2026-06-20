package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CarAdapterData(
    @JsonProperty("vin") String vin,
    @JsonProperty("vehicleId") Integer vehicleId,
    @JsonProperty("batteryCode") String batteryCode,
    @JsonProperty("brand") String brand,
    @JsonProperty("canGeneration") String canGeneration,
    @JsonProperty("carGateway") String carGateway,
    @JsonProperty("deliveryCountry") String deliveryCountry,
    @JsonProperty("deliveryDate") String deliveryDate,
    @JsonProperty("energy") String energy,
    @JsonProperty("engineType") String engineType,
    @JsonProperty("familyCode") String familyCode,
    @JsonProperty("firstRegistrationDate") String firstRegistrationDate,
    @JsonProperty("gearbox") String gearbox,
    @JsonProperty("modelCode") String modelCode,
    @JsonProperty("modelCodeDetail") String modelCodeDetail,
    @JsonProperty("modelName") String modelName,
    @JsonProperty("radioType") String radioType,
    @JsonProperty("region") String region,
    @JsonProperty("registrationCountry") String registrationCountry,
    @JsonProperty("registrationNumber") String registrationNumber,
    @JsonProperty("tcuCode") String tcuCode,
    @JsonProperty("versionCode") String versionCode,
    @JsonProperty("privacyMode") String privacyMode,
    @JsonProperty("privacyModeUpdateDate") String privacyModeUpdateDate,
    @JsonProperty("svtFlag") Boolean svtFlag,
    @JsonProperty("svtBlockFlag") Boolean svtBlockFlag
) {
    public boolean usesElectricity() { return "electric".equals(energy); }
    public boolean usesFuel() { return "gasoline".equals(energy); }
}
