package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KamereonPersonAccount(
    @JsonProperty("accountId") String accountId,
    @JsonProperty("accountType") String accountType,
    @JsonProperty("accountStatus") String accountStatus
) {}
