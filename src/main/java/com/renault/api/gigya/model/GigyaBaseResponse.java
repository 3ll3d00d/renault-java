package com.renault.api.gigya.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renault.api.exception.GigyaException;
import com.renault.api.exception.InvalidCredentialsException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GigyaBaseResponse {
    @JsonProperty("errorCode")
    private int errorCode;
    @JsonProperty("errorDetails")
    private String errorDetails;

    public int getErrorCode() { return errorCode; }
    public String getErrorDetails() { return errorDetails; }

    public void raiseForErrorCode() {
        if (errorCode == 0) return;
        if (errorCode == 403042) throw new InvalidCredentialsException(errorCode, errorDetails);
        throw new GigyaException(errorCode, errorDetails);
    }
}
