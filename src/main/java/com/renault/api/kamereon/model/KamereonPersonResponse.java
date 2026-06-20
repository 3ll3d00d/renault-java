package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KamereonPersonResponse extends KamereonResponse {
    @JsonProperty("accounts")
    private List<KamereonPersonAccount> accounts;

    public List<KamereonPersonAccount> getAccounts() { return accounts; }
}
