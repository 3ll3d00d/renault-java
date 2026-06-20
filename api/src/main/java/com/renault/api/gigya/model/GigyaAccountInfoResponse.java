package com.renault.api.gigya.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renault.api.exception.GigyaException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GigyaAccountInfoResponse extends GigyaBaseResponse {
    @JsonProperty("data")
    private GigyaAccountData data;

    public String getPersonId() {
        if (data == null) throw new GigyaException("`data` is null in GetAccountInfo response");
        if (data.personId() == null) throw new GigyaException("`data.personId` is null in GetAccountInfo response");
        return data.personId();
    }
}
