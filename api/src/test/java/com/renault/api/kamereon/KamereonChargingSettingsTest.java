package com.renault.api.kamereon;

import com.renault.api.TestFixtures;
import com.renault.api.kamereon.model.ChargeSchedule;
import com.renault.api.kamereon.model.ChargeDaySchedule;
import com.renault.api.kamereon.model.ChargingSettingsData;
import com.renault.api.kamereon.model.KamereonVehicleDataResponse;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/kamereon/test_kamereon_vehicle_action.py (charge schedule sections)
 *  and test_kamereon_vehicle_data.py (charging-settings sections). */
class KamereonChargingSettingsTest {

    private ChargingSettingsData loadSettings(String filename) {
        var response = TestFixtures.load(
            TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_data/" + filename,
            KamereonVehicleDataResponse.class);
        response.raiseForErrorCode();
        return response.getAttributes(ChargingSettingsData.class);
    }

    @Test
    void testSingleScheduleParsed() {
        var data = loadSettings("charging-settings.single.json");
        assertEquals("scheduled", data.mode);
        assertNotNull(data.schedules);
        assertEquals(1, data.schedules.size());

        var s = data.schedules.get(0);
        assertEquals(1, s.id);
        assertTrue(s.activated);
        assertNotNull(s.monday);    assertEquals("T12:00Z", s.monday.startTime());   assertEquals(15, s.monday.duration());
        assertNotNull(s.tuesday);   assertEquals("T04:30Z", s.tuesday.startTime());  assertEquals(420, s.tuesday.duration());
        assertNotNull(s.wednesday); assertEquals("T22:30Z", s.wednesday.startTime()); assertEquals(420, s.wednesday.duration());
        assertNotNull(s.thursday);  assertEquals("T22:00Z", s.thursday.startTime()); assertEquals(420, s.thursday.duration());
        assertNotNull(s.friday);    assertEquals("T12:15Z", s.friday.startTime());   assertEquals(15, s.friday.duration());
        assertNotNull(s.saturday);  assertEquals("T12:30Z", s.saturday.startTime()); assertEquals(30, s.saturday.duration());
        assertNotNull(s.sunday);    assertEquals("T12:45Z", s.sunday.startTime());   assertEquals(45, s.sunday.duration());
    }

    @Test
    void testMultiScheduleParsed() {
        var data = loadSettings("charging-settings.multi.json");
        assertEquals("scheduled", data.mode);
        assertNotNull(data.schedules);
        assertEquals(5, data.schedules.size());

        var s0 = data.schedules.get(0);
        assertEquals(1, s0.id);
        assertTrue(s0.activated);
        assertEquals("T00:00Z", s0.monday.startTime());
        assertEquals(450, s0.monday.duration());

        var s1 = data.schedules.get(1);
        assertEquals(2, s1.id);
        assertTrue(s1.activated);
        assertEquals("T23:30Z", s1.monday.startTime());
        assertEquals(15, s1.monday.duration());

        var s2 = data.schedules.get(2);
        assertEquals(3, s2.id);
        assertFalse(s2.activated);
        assertNull(s2.monday);
        assertNull(s2.tuesday);
    }

    @Test
    void testForJsonRoundtrip() {
        var data = loadSettings("charging-settings.multi.json");
        assertNotNull(data.schedules);

        var forJson = data.schedules.stream().map(ChargeSchedule::forJson).toList();

        assertEquals(5, forJson.size());
        assertScheduleJson(forJson.get(0), 1, true, "T00:00Z", 450);
        assertScheduleJson(forJson.get(1), 2, true, "T23:30Z", 15);
        assertEmptyScheduleJson(forJson.get(2), 3);
        assertEmptyScheduleJson(forJson.get(3), 4);
        assertEmptyScheduleJson(forJson.get(4), 5);
    }

    @Test
    void testUpdateDay() {
        var data = loadSettings("charging-settings.multi.json");

        Map<String, Object> update = new HashMap<>();
        update.put("id", 1);
        update.put("tuesday", Map.of("startTime", "T12:00Z", "duration", 15));
        update.put("wednesday", null);
        data.update(update);

        var s0 = data.schedules.get(0);
        // Monday unchanged
        assertNotNull(s0.monday);
        assertEquals("T00:00Z", s0.monday.startTime());
        assertEquals(450, s0.monday.duration());
        // Tuesday updated
        assertNotNull(s0.tuesday);
        assertEquals("T12:00Z", s0.tuesday.startTime());
        assertEquals(15, s0.tuesday.duration());
        // Wednesday cleared
        assertNull(s0.wednesday);
    }

    @Test
    void testUpdateActivated() {
        var data = loadSettings("charging-settings.multi.json");

        assertTrue(data.schedules.get(1).activated);
        data.update(Map.of("id", 2, "activated", false));
        assertFalse(data.schedules.get(1).activated);
    }

    @Test
    void testForJsonAfterUpdate() {
        var data = loadSettings("charging-settings.multi.json");

        // Apply updates matching the Python test
        Map<String, Object> upd1 = new HashMap<>();
        upd1.put("id", 1);
        upd1.put("tuesday", Map.of("startTime", "T12:00Z", "duration", 15));
        upd1.put("wednesday", null);
        data.update(upd1);
        data.update(Map.of("id", 2, "activated", false));

        var forJson = data.schedules.stream().map(ChargeSchedule::forJson).toList();
        var s0 = forJson.get(0);
        assertEquals(Map.of("startTime", "T00:00Z", "duration", 450), s0.get("monday"));
        assertEquals(Map.of("startTime", "T12:00Z", "duration", 15),  s0.get("tuesday"));
        assertNull(s0.get("wednesday"));
        assertEquals(Map.of("startTime", "T00:00Z", "duration", 450), s0.get("thursday"));

        var s1 = forJson.get(1);
        assertEquals(false, s1.get("activated"));
        assertEquals(Map.of("startTime", "T23:30Z", "duration", 15), s1.get("monday"));
    }

    // ---- helpers ----

    private void assertScheduleJson(Map<String, Object> s, int id, boolean activated, String startTime, int duration) {
        assertEquals(id, s.get("id"));
        assertEquals(activated, s.get("activated"));
        Map<String, Object> expectedDay = Map.of("startTime", startTime, "duration", duration);
        for (String day : ChargeSchedule.DAYS) {
            assertEquals(expectedDay, s.get(day), "Day " + day + " mismatch");
        }
    }

    private void assertEmptyScheduleJson(Map<String, Object> s, int id) {
        assertEquals(id, s.get("id"));
        assertEquals(false, s.get("activated"));
        for (String day : ChargeSchedule.DAYS) {
            assertNull(s.get(day), "Day " + day + " should be null");
        }
    }
}
