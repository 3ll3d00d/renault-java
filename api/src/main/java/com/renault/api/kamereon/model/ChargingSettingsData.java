package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChargingSettingsData {
    @JsonProperty("mode") public String mode;
    @JsonProperty("schedules") public List<ChargeSchedule> schedules;
    @JsonProperty("startDateTime") public String startDateTime;
    @JsonProperty("dateTime") public String dateTime;
    @JsonProperty("delay") public Integer delay;

    public ChargingSettingsData() {}

    /** Update a specific schedule by ID, replacing day slots or the activated flag. */
    @SuppressWarnings("unchecked")
    public void update(Map<String, Object> settings) {
        int targetId = ((Number) settings.get("id")).intValue();
        if (schedules == null) return;
        for (ChargeSchedule schedule : schedules) {
            if (!Objects.equals(schedule.id, targetId)) continue;
            if (settings.containsKey("activated")) {
                schedule.activated = (Boolean) settings.get("activated");
            }
            for (String day : ChargeSchedule.DAYS) {
                if (!settings.containsKey(day)) continue;
                Object dayVal = settings.get(day);
                if (dayVal == null) {
                    schedule.setDay(day, null);
                } else {
                    Map<String, Object> dm = (Map<String, Object>) dayVal;
                    schedule.setDay(day, new ChargeDaySchedule(
                        (String) dm.get("startTime"),
                        ((Number) dm.get("duration")).intValue()
                    ));
                }
            }
            return;
        }
    }
}
