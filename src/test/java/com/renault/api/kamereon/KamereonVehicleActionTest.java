package com.renault.api.kamereon;

import com.renault.api.TestFixtures;
import com.renault.api.kamereon.model.KamereonVehicleDataResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/kamereon/test_kamereon_vehicle_action.py */
class KamereonVehicleActionTest {

    private KamereonVehicleDataResponse load(String filename) {
        return TestFixtures.load(
            TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_action/" + filename,
            KamereonVehicleDataResponse.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "hvac-start.start.json",
        "hvac-start.cancel.json",
        "hvac-start.stop.json",
        "charging-start.start.json",
        "charging-start.stop.json",
        "charge-mode.schedule_mode.json",
        "charge-schedule.scheduled.json",
    })
    void testActionResponseNoError(String filename) {
        var response = load(filename);
        assertDoesNotThrow(response::raiseForErrorCode);
        assertNotNull(response.getData());
        assertEquals("guid", response.getData().id());
    }

    @Test
    void testHvacStartAttributes() {
        var response = load("hvac-start.start.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var attrs = response.getData().attributes();
        assertNotNull(attrs);
        assertEquals("start", attrs.get("action").asText());
        assertEquals(21.0, attrs.get("targetTemperature").asDouble());
    }

    @Test
    void testChargingStartAttributes() {
        var response = load("charging-start.start.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var attrs = response.getData().attributes();
        assertNotNull(attrs);
        assertEquals("start", attrs.get("action").asText());
    }
}
