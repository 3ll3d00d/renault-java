package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HvacDaySchedule(
    @JsonProperty("readyAtTime") String readyAtTime
) {
    public Map<String, Object> forJson() {
        var m = new HashMap<String, Object>();
        m.put("readyAtTime", readyAtTime);
        return m;
    }
}
