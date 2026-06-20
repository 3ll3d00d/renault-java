package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/// HVAC pre-conditioning target for a single day. `readyAtTime` uses `"THH:mmZ"`
/// UTC format (e.g. `"T07:00Z"`): the cabin will be at the target temperature by this time.
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
