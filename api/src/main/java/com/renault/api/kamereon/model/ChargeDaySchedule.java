package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/// Charging window for a single day. `startTime` uses `"THH:mmZ"` UTC format
/// (e.g. `"T23:30Z"`); `duration` is in minutes.
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChargeDaySchedule(
    @JsonProperty("startTime") String startTime,
    @JsonProperty("duration") Integer duration
) {
    public Map<String, Object> forJson() {
        var m = new HashMap<String, Object>();
        m.put("startTime", startTime);
        m.put("duration", duration);
        return m;
    }
}
