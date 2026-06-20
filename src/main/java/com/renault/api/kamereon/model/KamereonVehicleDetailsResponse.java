package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Kamereon response to GET on /accounts/{accountId}/vehicles/{vin}/details. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class KamereonVehicleDetailsResponse extends KamereonVehicleDetails {}
