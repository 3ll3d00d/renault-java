package com.renault.api.kamereon;

import com.renault.api.TestFixtures;
import com.renault.api.kamereon.model.HvacSchedule;
import com.renault.api.kamereon.model.HvacSettingsData;
import com.renault.api.kamereon.model.KamereonVehicleDataResponse;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/kamereon/test_kamereon_vehicle_action.py (hvac schedule sections)
 *  and test_kamereon_vehicle_data.py (hvac-settings sections). */
class KamereonHvacSettingsTest {

    private HvacSettingsData loadSettings() {
        var response = TestFixtures.load(
            TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_data/hvac-settings.json",
            KamereonVehicleDataResponse.class);
        response.raiseForErrorCode();
        return response.getAttributes(HvacSettingsData.class);
    }

    @Test
    void testMode() {
        var data = loadSettings();
        assertEquals("scheduled", data.mode);
    }

    @Test
    void testSchedules() {
        var data = loadSettings();
        assertNotNull(data.schedules);
        assertEquals(5, data.schedules.size());

        // Schedule 2 (index 1) has Wednesday and Friday
        var s1 = data.schedules.get(1);
        assertEquals(2, s1.id);
        assertNotNull(s1.wednesday);
        assertEquals("T15:15Z", s1.wednesday.readyAtTime());
        assertNotNull(s1.friday);
        assertEquals("T15:15Z", s1.friday.readyAtTime());
        assertNull(s1.monday);

        // All other schedules (0, 2, 3, 4) have no day entries
        for (int i : new int[]{0, 2, 3, 4}) {
            var s = data.schedules.get(i);
            assertEquals(i + 1, s.id);
            for (String day : HvacSchedule.DAYS) {
                assertNull(s.getDay(day), "Schedule " + s.id + " day " + day + " should be null");
            }
        }
    }

    @Test
    void testForJsonRoundtrip() {
        var data = loadSettings();
        assertNotNull(data.schedules);

        var forJson = data.schedules.stream().map(HvacSchedule::forJson).toList();
        assertEquals(5, forJson.size());

        // Schedule 1 — all nulls
        assertEmptyHvacScheduleJson(forJson.get(0), 1, false);
        // Schedule 2 — wednesday and friday set
        var s1 = forJson.get(1);
        assertEquals(2, s1.get("id"));
        assertEquals(true, s1.get("activated"));
        assertEquals(Map.of("readyAtTime", "T15:15Z"), s1.get("wednesday"));
        assertEquals(Map.of("readyAtTime", "T15:15Z"), s1.get("friday"));
        assertNull(s1.get("monday"));
        assertNull(s1.get("tuesday"));
        assertNull(s1.get("thursday"));
        assertNull(s1.get("saturday"));
        assertNull(s1.get("sunday"));
        // Schedules 3–5 — all nulls
        assertEmptyHvacScheduleJson(forJson.get(2), 3, false);
        assertEmptyHvacScheduleJson(forJson.get(3), 4, false);
        assertEmptyHvacScheduleJson(forJson.get(4), 5, false);
    }

    @Test
    void testUpdateDays() {
        var data = loadSettings();

        Map<String, Object> update = new HashMap<>();
        update.put("id", 1);
        update.put("sunday", Map.of("readyAtTime", "T20:30Z"));
        update.put("tuesday", Map.of("readyAtTime", "T20:30Z"));
        update.put("thursday", null);
        data.update(update);

        var s0 = data.schedules.get(0);
        assertNull(s0.monday);
        assertNotNull(s0.tuesday);   assertEquals("T20:30Z", s0.tuesday.readyAtTime());
        assertNull(s0.wednesday);
        assertNull(s0.thursday);
        assertNull(s0.friday);
        assertNull(s0.saturday);
        assertNotNull(s0.sunday);    assertEquals("T20:30Z", s0.sunday.readyAtTime());
    }

    @Test
    void testUpdateActivated() {
        var data = loadSettings();

        assertTrue(data.schedules.get(1).activated);
        data.update(Map.of("id", 2, "activated", false));
        assertFalse(data.schedules.get(1).activated);
    }

    // ---- helper ----

    private void assertEmptyHvacScheduleJson(Map<String, Object> s, int id, boolean activated) {
        assertEquals(id, s.get("id"));
        assertEquals(activated, s.get("activated"));
        for (String day : HvacSchedule.DAYS) {
            assertNull(s.get(day), "Day " + day + " should be null");
        }
    }
}
