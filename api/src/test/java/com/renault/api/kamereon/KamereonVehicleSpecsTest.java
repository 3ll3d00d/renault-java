package com.renault.api.kamereon;

import com.fasterxml.jackson.databind.JsonNode;
import com.renault.api.TestFixtures;
import com.renault.api.kamereon.model.KamereonVehicleDetails;
import com.renault.api.kamereon.model.KamereonVehicleDetailsResponse;
import com.renault.api.kamereon.model.KamereonVehiclesResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Mirrors Python tests/kamereon/test_kamereon_vehicles.py and test_kamereon_vehicle_details.py.
 * Verifies that KamereonVehicleDetails returns the same property values as the Python library
 * for each vehicle fixture defined in expected_specs.json.
 */
class KamereonVehicleSpecsTest {
    private static final String VEHICLES_PATH = TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicles/";
    private static final String DETAILS_PATH  = TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_details/";

    record VehicleFixture(String filename, JsonNode expected) {}

    static Stream<VehicleFixture> vehicleFixtures() {
        JsonNode specs;
        try {
            specs = TestFixtures.MAPPER.readTree(
                TestFixtures.load(TestFixtures.KAMEREON_FIXTURE_PATH + "/expected_specs.json"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load expected_specs.json", e);
        }
        var builder = Stream.<VehicleFixture>builder();
        specs.fields().forEachRemaining(entry ->
            builder.accept(new VehicleFixture(entry.getKey(), entry.getValue())));
        return builder.build();
    }

    private KamereonVehicleDetails loadFromVehiclesFixture(String filename) {
        var response = TestFixtures.load(VEHICLES_PATH + filename, KamereonVehiclesResponse.class);
        var links = response.getVehicleLinks();
        assertNotNull(links, "vehicleLinks must not be null in " + filename);
        assertFalse(links.isEmpty(), "vehicleLinks must not be empty in " + filename);
        var details = links.get(0).vehicleDetails();
        assertNotNull(details, "vehicleDetails must not be null in " + filename);
        return details;
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("vehicleFixtures")
    void testModelLabels(VehicleFixture fixture) {
        var details = loadFromVehiclesFixture(fixture.filename());
        var exp = fixture.expected();

        assertEquals(exp.get("get_brand_label").asText(), details.getBrandLabel(),
            fixture.filename() + ": brand label mismatch");
        assertEquals(exp.get("get_energy_code").asText(), details.getEnergyCode(),
            fixture.filename() + ": energy code mismatch");
        assertEquals(exp.get("get_model_code").asText(), details.getModelCode(),
            fixture.filename() + ": model code mismatch");
        assertEquals(exp.get("get_model_label").asText(), details.getModelLabel(),
            fixture.filename() + ": model label mismatch");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("vehicleFixtures")
    void testEnergyType(VehicleFixture fixture) {
        var details = loadFromVehiclesFixture(fixture.filename());
        var exp = fixture.expected();

        assertEquals(exp.get("uses_electricity").asBoolean(), details.usesElectricity(),
            fixture.filename() + ": uses_electricity mismatch");
        assertEquals(exp.get("uses_fuel").asBoolean(), details.usesFuel(),
            fixture.filename() + ": uses_fuel mismatch");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("vehicleFixtures")
    void testEndpointSupport(VehicleFixture fixture) {
        var details = loadFromVehiclesFixture(fixture.filename());
        var exp = fixture.expected();

        assertEquals(exp.get("supports-hvac-status").asBoolean(), details.supportsEndpoint("hvac-status"),
            fixture.filename() + ": supports-hvac-status mismatch");
        assertEquals(exp.get("supports-location").asBoolean(), details.supportsEndpoint("location"),
            fixture.filename() + ": supports-location mismatch");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("vehicleFixtures")
    void testChargingPowerReporting(VehicleFixture fixture) {
        var details = loadFromVehiclesFixture(fixture.filename());
        var exp = fixture.expected();

        assertEquals(exp.get("reports_charging_power_in_watts").asBoolean(), details.reportsChargingPowerInWatts(),
            fixture.filename() + ": reports_charging_power_in_watts mismatch");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("vehicleFixtures")
    void testChargeUsesKcm(VehicleFixture fixture) {
        var details = loadFromVehiclesFixture(fixture.filename());
        var exp = fixture.expected();

        boolean chargeUsesKcm = VehicleEndpoints.getSpecification(details.getModelCode(), "control-charge-via-kcm");
        assertEquals(exp.get("charge-uses-kcm").asBoolean(), chargeUsesKcm,
            fixture.filename() + ": charge-uses-kcm mismatch");
    }

    // ---- vehicle_details endpoint fixtures (subset with standalone detail responses) ----

    @ParameterizedTest(name = "{0}")
    @MethodSource("vehicleDetailsFixtures")
    void testVehicleDetailsEndpoint(VehicleFixture fixture) {
        var details = TestFixtures.load(DETAILS_PATH + fixture.filename(), KamereonVehicleDetailsResponse.class);
        var exp = fixture.expected();

        assertEquals(exp.get("get_brand_label").asText(), details.getBrandLabel(),
            fixture.filename() + " (details): brand label mismatch");
        assertEquals(exp.get("get_model_code").asText(), details.getModelCode(),
            fixture.filename() + " (details): model code mismatch");
        assertEquals(exp.get("get_model_label").asText(), details.getModelLabel(),
            fixture.filename() + " (details): model label mismatch");
        assertEquals(exp.get("get_energy_code").asText(), details.getEnergyCode(),
            fixture.filename() + " (details): energy code mismatch");
    }

    static Stream<VehicleFixture> vehicleDetailsFixtures() {
        var detailsFixtures = new String[]{"master_iv.1.json", "megane_e-tech.2.json", "zoe_40.1.json"};
        JsonNode specs;
        try {
            specs = TestFixtures.MAPPER.readTree(
                TestFixtures.load(TestFixtures.KAMEREON_FIXTURE_PATH + "/expected_specs.json"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load expected_specs.json", e);
        }
        return Stream.of(detailsFixtures)
            .filter(f -> specs.has(f))
            .map(f -> new VehicleFixture(f, specs.get(f)));
    }
}
