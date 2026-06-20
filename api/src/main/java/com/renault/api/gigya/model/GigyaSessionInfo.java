package com.renault.api.gigya.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GigyaSessionInfo(
    @JsonProperty("cookieValue") String cookieValue
) {}
