package com.renault.api.kamereon;

import com.renault.api.TestFixtures;
import com.renault.api.exception.KamereonException;
import com.renault.api.kamereon.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/kamereon/test_kamereon_vehicle_data.py */
class KamereonVehicleDataTest {

    private KamereonVehicleDataResponse load(String filename) {
        return TestFixtures.load(
            TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_data/" + filename,
            KamereonVehicleDataResponse.class);
    }

    @Test
    void testBatteryStatus1() {
        var response = load("battery-status.1.json");
        assertDoesNotThrow(response::raiseForErrorCode);
        assertNotNull(response.getData());
        assertTrue(response.getData().id().startsWith("VF1"));

        var data = response.getAttributes(BatteryStatusData.class);
        assertEquals("2020-11-17T09:06:48+01:00", data.timestamp());
        assertEquals(50, data.batteryLevel());
        assertNull(data.batteryTemperature());
        assertEquals(128, data.batteryAutonomy());
        assertEquals(0, data.batteryCapacity());
        assertEquals(0, data.batteryAvailableEnergy());
        assertEquals(0, data.plugStatus());
        assertEquals(-1.0, data.chargingStatus());
        assertNull(data.chargingRemainingTime());
        assertNull(data.chargingInstantaneousPower());
    }

    @Test
    void testBatteryStatus2() {
        var response = load("battery-status.2.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(BatteryStatusData.class);
        assertEquals("2020-01-12T21:40:16Z", data.timestamp());
        assertEquals(60, data.batteryLevel());
        assertEquals(20, data.batteryTemperature());
        assertEquals(141, data.batteryAutonomy());
        assertEquals(0, data.batteryCapacity());
        assertEquals(31, data.batteryAvailableEnergy());
        assertEquals(1, data.plugStatus());
        assertEquals(1.0, data.chargingStatus());
        assertEquals(145, data.chargingRemainingTime());
        assertEquals(27.0, data.chargingInstantaneousPower());
    }

    @Test
    void testTyrePressure() {
        var response = load("pressure.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(TyrePressureData.class);
        assertEquals(2460, data.flPressure());
        assertEquals(2730, data.frPressure());
        assertEquals(2790, data.rlPressure());
        assertEquals(2790, data.rrPressure());
        assertEquals(0, data.flStatus());
        assertEquals(0, data.frStatus());
        assertEquals(0, data.rlStatus());
        assertEquals(0, data.rrStatus());
    }

    @Test
    void testCockpitZoe() {
        var response = load("cockpit.zoe.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(CockpitData.class);
        assertEquals(49114.27, data.totalMileage());
        assertNull(data.fuelAutonomy());
        assertNull(data.fuelQuantity());
    }

    @Test
    void testCockpitCapturII() {
        var response = load("cockpit.captur_ii.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(CockpitData.class);
        assertEquals(5566.78, data.totalMileage());
        assertEquals(35.0, data.fuelAutonomy());
        assertEquals(3.0, data.fuelQuantity());
    }

    @Test
    void testLocationV1() {
        var response = load("location.1.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(LocationData.class);
        assertEquals(48.1234567, data.gpsLatitude());
        assertEquals(11.1234567, data.gpsLongitude());
        assertEquals("2020-02-18T16:58:38Z", data.lastUpdateTime());
    }

    @Test
    void testLocationV2() {
        var response = load("location.2.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(LocationData.class);
        assertEquals(48.1234567, data.gpsLatitude());
        assertEquals(11.1234567, data.gpsLongitude());
        assertEquals("2020-02-18T16:58:38Z", data.lastUpdateTime());
    }

    @Test
    void testLockStatusLocked() {
        var response = load("lock-status.1.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(LockStatusData.class);
        assertEquals("locked", data.lockStatus());
        assertEquals("closed", data.doorStatusRearLeft());
        assertEquals("closed", data.doorStatusRearRight());
        assertEquals("closed", data.doorStatusDriver());
        assertEquals("closed", data.doorStatusPassenger());
        assertEquals("closed", data.hatchStatus());
        assertEquals("2022-02-02T13:51:13Z", data.lastUpdateTime());
    }

    @Test
    void testLockStatusUnlocked() {
        var response = load("lock-status.2.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(LockStatusData.class);
        assertEquals("unlocked", data.lockStatus());
        assertEquals("closed", data.doorStatusRearLeft());
        assertEquals("closed", data.doorStatusRearRight());
        assertEquals("closed", data.doorStatusDriver());
        assertEquals("closed", data.doorStatusPassenger());
        assertEquals("closed", data.hatchStatus());
        assertEquals("2022-02-02T13:51:13Z", data.lastUpdateTime());
    }

    @Test
    void testResStateStopped() {
        var response = load("res-state.1.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(ResStateData.class);
        assertEquals("Stopped, ready for RES", data.details());
        assertEquals("10", data.code());
    }

    @Test
    void testResStateRunning() {
        var response = load("res-state.2.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(ResStateData.class);
        assertEquals("Running", data.details());
        assertEquals("42", data.code());
    }

    @Test
    void testChargeMode() {
        var response = load("charge-mode.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(ChargeModeData.class);
        assertEquals("always", data.chargeMode());
    }

    @Test
    void testHvacStatusZoe() {
        var response = load("hvac-status.zoe.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(HvacStatusData.class);
        assertEquals("off", data.hvacStatus());
        assertEquals(8.0, data.externalTemperature());
        assertNull(data.socThreshold());
    }

    @Test
    void testHvacStatusZoe50() {
        var response = load("hvac-status.zoe_50.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(HvacStatusData.class);
        assertEquals("on", data.hvacStatus());
        assertEquals(40.0, data.socThreshold());
        assertNull(data.externalTemperature());
    }

    // ---- Renault 5 (R5E1VE) live fixtures ----

    @Test
    void testBatteryStatusR5() {
        var response = load("battery-status.vysp0100.json");
        assertDoesNotThrow(response::raiseForErrorCode);
        assertNotNull(response.getData());
        assertTrue(response.getData().id().startsWith("VYSP"));

        var data = response.getAttributes(BatteryStatusData.class);
        assertEquals("2026-06-21T12:53:46Z", data.timestamp());
        assertEquals(80, data.batteryLevel());
        assertEquals(261, data.batteryAutonomy());
        assertEquals(0, data.plugStatus());
        assertEquals(0.0, data.chargingStatus());
        assertEquals(3, data.chargingRemainingTime());
        assertEquals(0, data.v2lSystemStatusDisplay());
        assertNull(data.batteryTemperature());
        assertNull(data.batteryCapacity());
        assertNull(data.batteryAvailableEnergy());
        assertNull(data.chargingInstantaneousPower());
    }

    @Test
    void testCockpitR5() {
        var response = load("cockpit.vysp0100.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(CockpitData.class);
        assertEquals(3841.0, data.totalMileage());
        assertEquals(0.0, data.fuelQuantity());
        assertNull(data.fuelAutonomy());
    }

    @Test
    void testHvacStatusR5() {
        var response = load("hvac-status.vysp0100.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(HvacStatusData.class);
        assertEquals("off", data.hvacStatus());
        assertEquals(15.0, data.socThreshold());
        assertEquals(0.0, data.internalTemperature());
        assertEquals("2026-06-21T12:34:27Z", data.lastUpdateTime());
        assertNull(data.externalTemperature());
    }

    @Test
    void testHvacSettingsR5Error() {
        var response = load("hvac-settings.vysp0100.json");
        assertThrows(KamereonException.class, response::raiseForErrorCode);
    }

    @Test
    void testLocationR5() {
        var response = load("location.vysp0100.json");
        assertDoesNotThrow(response::raiseForErrorCode);

        var data = response.getAttributes(LocationData.class);
        assertEquals(48.1234567, data.gpsLatitude(), 1e-10);
        assertEquals(2.3456789, data.gpsLongitude(), 1e-10);
        assertEquals("2026-06-21T12:52:28Z", data.lastUpdateTime());
    }

    @Test
    void testChargesR5() {
        var response = load("charges.vysp0100.json");
        assertDoesNotThrow(response::raiseForErrorCode);
        assertNotNull(response.getData());
        assertEquals("VYSP0100012345678", response.getData().id());
    }

    @Test
    void testChargeHistoryR5Error() {
        var response = load("charge-history.vysp0100.json");
        assertThrows(KamereonException.class, response::raiseForErrorCode);
    }

    @Test
    void testHvacHistoryR5Error() {
        var response = load("hvac-history.vysp0100.json");
        assertThrows(KamereonException.class, response::raiseForErrorCode);
    }

    @Test
    void testHvacSessionsR5Error() {
        var response = load("hvac-sessions.vysp0100.json");
        assertThrows(KamereonException.class, response::raiseForErrorCode);
    }

    @Test
    void testNoData() {
        var response = load("no_data.json");
        assertDoesNotThrow(response::raiseForErrorCode);
        assertNotNull(response.getData());
        assertEquals("ChargeMode", response.getData().type());

        var data = response.getAttributes(CockpitData.class);
        assertNull(data.totalMileage());
        assertNull(data.fuelAutonomy());
        assertNull(data.fuelQuantity());
    }
}
