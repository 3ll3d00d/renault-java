package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.Map;

/// A weekly charging schedule program. Each program has an ID, an activated flag, and an
/// optional {@link ChargeDaySchedule} per day of the week. Pass a list of these to
/// {@link com.renault.api.RenaultVehicle#setChargeSchedules}.
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChargeSchedule {
    public static final String[] DAYS = {"monday","tuesday","wednesday","thursday","friday","saturday","sunday"};

    @JsonProperty("id") public Integer id;
    @JsonProperty("activated") public Boolean activated;
    @JsonProperty("monday") public ChargeDaySchedule monday;
    @JsonProperty("tuesday") public ChargeDaySchedule tuesday;
    @JsonProperty("wednesday") public ChargeDaySchedule wednesday;
    @JsonProperty("thursday") public ChargeDaySchedule thursday;
    @JsonProperty("friday") public ChargeDaySchedule friday;
    @JsonProperty("saturday") public ChargeDaySchedule saturday;
    @JsonProperty("sunday") public ChargeDaySchedule sunday;

    public ChargeSchedule() {}

    public ChargeDaySchedule getDay(String day) {
        return switch (day) {
            case "monday"    -> monday;
            case "tuesday"   -> tuesday;
            case "wednesday" -> wednesday;
            case "thursday"  -> thursday;
            case "friday"    -> friday;
            case "saturday"  -> saturday;
            case "sunday"    -> sunday;
            default -> null;
        };
    }

    public void setDay(String day, ChargeDaySchedule value) {
        switch (day) {
            case "monday"    -> monday = value;
            case "tuesday"   -> tuesday = value;
            case "wednesday" -> wednesday = value;
            case "thursday"  -> thursday = value;
            case "friday"    -> friday = value;
            case "saturday"  -> saturday = value;
            case "sunday"    -> sunday = value;
        }
    }

    public Map<String, Object> forJson() {
        var m = new LinkedHashMap<String, Object>();
        m.put("id", id);
        m.put("activated", activated);
        for (String day : DAYS) {
            ChargeDaySchedule spec = getDay(day);
            m.put(day, spec != null ? spec.forJson() : null);
        }
        return m;
    }
}
