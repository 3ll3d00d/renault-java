package com.renault.api.gigya.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renault.api.exception.GigyaException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GigyaLoginResponse extends GigyaBaseResponse {
    @JsonProperty("sessionInfo")
    private GigyaSessionInfo sessionInfo;

    public String getSessionCookie() {
        if (sessionInfo == null) throw new GigyaException("`sessionInfo` is null in Login response");
        if (sessionInfo.cookieValue() == null) throw new GigyaException("`sessionInfo.cookieValue` is null in Login response");
        return sessionInfo.cookieValue();
    }
}
