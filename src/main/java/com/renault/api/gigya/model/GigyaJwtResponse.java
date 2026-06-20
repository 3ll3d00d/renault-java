package com.renault.api.gigya.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renault.api.exception.GigyaException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GigyaJwtResponse extends GigyaBaseResponse {
    @JsonProperty("id_token")
    private String idToken;

    public String getJwt() {
        if (idToken == null) throw new GigyaException("`id_token` is null in GetJWT response");
        return idToken;
    }
}
