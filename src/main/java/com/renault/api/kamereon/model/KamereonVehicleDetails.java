package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renault.api.kamereon.EndpointDefinition;
import com.renault.api.kamereon.VehicleEndpoints;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KamereonVehicleDetails extends KamereonResponse {
    @JsonProperty("vin") protected String vin;
    @JsonProperty("registrationNumber") protected String registrationNumber;
    @JsonProperty("radioCode") protected String radioCode;
    @JsonProperty("brand") protected KamereonVehicleDetailsGroup brand;
    @JsonProperty("model") protected KamereonVehicleDetailsGroup model;
    @JsonProperty("energy") protected KamereonVehicleDetailsGroup energy;
    @JsonProperty("engineEnergyType") protected String engineEnergyType;
    @JsonProperty("assets") protected List<Map<String, Object>> assets;

    public String getVin() { return vin; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getRadioCode() { return radioCode; }
    public KamereonVehicleDetailsGroup getBrand() { return brand; }
    public KamereonVehicleDetailsGroup getModel() { return model; }
    public KamereonVehicleDetailsGroup getEnergy() { return energy; }
    public String getEngineEnergyType() { return engineEnergyType; }
    public List<Map<String, Object>> getAssets() { return assets; }

    public String getModelCode() { return model != null ? model.code() : null; }
    public String getModelLabel() { return model != null ? model.label() : null; }
    public String getBrandLabel() { return brand != null ? brand.label() : null; }
    public String getEnergyCode() { return energy != null ? energy.code() : null; }

    public boolean usesElectricity() {
        String et = engineEnergyType != null ? engineEnergyType : getEnergyCode();
        return "ELEC".equals(et) || "ELECX".equals(et) || "PHEV".equals(et);
    }

    public boolean usesFuel() {
        String et = engineEnergyType != null ? engineEnergyType : getEnergyCode();
        return "OTHER".equals(et) || "PHEV".equals(et) || "HEV".equals(et);
    }

    public boolean reportsChargeSessionDurationsInMinutes() {
        return VehicleEndpoints.getSpecification(getModelCode(), "reports-charge-session-durations-in-minutes");
    }

    public boolean reportsChargingPowerInWatts() {
        return VehicleEndpoints.getSpecification(getModelCode(), "reports-in-watts");
    }

    public boolean supportsEndpoint(String endpoint) {
        return getEndpoint(endpoint) != null;
    }

    public Map<String, EndpointDefinition> getEndpoints() {
        return VehicleEndpoints.getModelEndpoints(getModelCode());
    }

    public EndpointDefinition getEndpoint(String endpoint) {
        return VehicleEndpoints.getModelEndpoint(getModelCode(), endpoint);
    }
}
