package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class HvacSettingsData {
    @JsonProperty("mode") public String mode;
    @JsonProperty("schedules") public List<HvacSchedule> schedules;

    public HvacSettingsData() {}

    /** Update a specific schedule by ID, replacing day slots or the activated flag. */
    @SuppressWarnings("unchecked")
    public void update(Map<String, Object> settings) {
        int targetId = ((Number) settings.get("id")).intValue();
        if (schedules == null) return;
        for (HvacSchedule schedule : schedules) {
            if (!Objects.equals(schedule.id, targetId)) continue;
            if (settings.containsKey("activated")) {
                schedule.activated = (Boolean) settings.get("activated");
            }
            for (String day : HvacSchedule.DAYS) {
                if (!settings.containsKey(day)) continue;
                Object dayVal = settings.get(day);
                if (dayVal == null) {
                    schedule.setDay(day, null);
                } else {
                    Map<String, Object> dm = (Map<String, Object>) dayVal;
                    schedule.setDay(day, new HvacDaySchedule((String) dm.get("readyAtTime")));
                }
            }
            return;
        }
    }
}
