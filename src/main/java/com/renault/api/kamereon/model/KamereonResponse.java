package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KamereonResponse {
    @JsonProperty("errors")
    protected List<KamereonResponseError> errors;

    public List<KamereonResponseError> getErrors() { return errors; }

    public void raiseForErrorCode() {
        if (errors == null) return;
        for (KamereonResponseError error : errors) {
            error.raiseForErrorCode();
        }
    }
}
