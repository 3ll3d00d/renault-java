package com.renault.harness;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import com.renault.api.RenaultVehicle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/// Runs named read endpoints against a live vehicle and compares to fixture JSON.
public class EndpointRunner {

    public record Result(
        String endpoint,
        String liveJson,
        String fixtureName,
        String fixtureJson,
        List<StructuralDiff.Entry> diff
    ) {}

    // Map endpoint name → fixture file prefix within vehicle_data/
    private static final Map<String, String> FIXTURE_PREFIX = new LinkedHashMap<>();
    static {
        FIXTURE_PREFIX.put("battery-status",      "battery-status");
        FIXTURE_PREFIX.put("battery-soc",         "battery-soc");
        FIXTURE_PREFIX.put("cockpit",             "cockpit");
        FIXTURE_PREFIX.put("location",            "location");
        FIXTURE_PREFIX.put("lock-status",         "lock-status");
        FIXTURE_PREFIX.put("hvac-status",         "hvac-status");
        FIXTURE_PREFIX.put("hvac-settings",       "hvac-settings");
        FIXTURE_PREFIX.put("charge-mode",         "charge-mode");
        FIXTURE_PREFIX.put("charging-settings",   "charging-settings");
        FIXTURE_PREFIX.put("tyre-pressure",       "pressure");
        FIXTURE_PREFIX.put("res-state",           "res-state");
        FIXTURE_PREFIX.put("charge-schedule",     "charge-schedule");
    }

    public static List<String> endpoints() {
        return List.copyOf(FIXTURE_PREFIX.keySet());
    }

    private final RenaultVehicle vehicle;
    private final Path fixtureDir;
    private final ObjectMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();

    public EndpointRunner(RenaultVehicle vehicle, Path fixtureDir) {
        this.vehicle = vehicle;
        this.fixtureDir = fixtureDir;
    }

    public Result run(String endpoint) throws Exception {
        Object raw = switch (endpoint) {
            case "battery-status"    -> vehicle.getBatteryStatus();
            case "battery-soc"       -> vehicle.getBatterySoc();
            case "cockpit"           -> vehicle.getCockpit();
            case "location"          -> vehicle.getLocation();
            case "lock-status"       -> vehicle.getLockStatus();
            case "hvac-status"       -> vehicle.getHvacStatus();
            case "hvac-settings"     -> vehicle.getHvacSettings();
            case "charge-mode"       -> vehicle.getChargeMode();
            case "charging-settings" -> vehicle.getChargingSettings();
            case "tyre-pressure"     -> vehicle.getTyrePressure();
            case "res-state"         -> vehicle.getResState();
            case "charge-schedule"   -> vehicle.getChargeSchedule();
            default -> throw new IllegalArgumentException("Unknown endpoint: " + endpoint);
        };

        String liveJson = mapper.writeValueAsString(raw);
        JsonNode liveNode = mapper.readTree(liveJson);

        String prefix = FIXTURE_PREFIX.get(endpoint);
        var fixture = findFixture(prefix);
        if (fixture == null) {
            return new Result(endpoint, liveJson, null, null, List.of());
        }

        String fixtureJson = Files.readString(fixture);
        // fixtures wrap data; try to unwrap attributes node if present
        JsonNode fixtureNode = mapper.readTree(fixtureJson);
        JsonNode attrs = fixtureNode.path("data").path("attributes");
        JsonNode fixtureAttrs = attrs.isMissingNode() ? fixtureNode : attrs;

        List<StructuralDiff.Entry> diff = StructuralDiff.compare(liveNode, fixtureAttrs);
        return new Result(endpoint, liveJson, fixture.getFileName().toString(),
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fixtureAttrs), diff);
    }

    private Path findFixture(String prefix) throws IOException {
        if (!Files.isDirectory(fixtureDir)) return null;
        try (Stream<Path> files = Files.list(fixtureDir)) {
            return files
                .filter(p -> p.getFileName().toString().startsWith(prefix) && p.toString().endsWith(".json"))
                .min(Path::compareTo)   // pick first alphabetically
                .orElse(null);
        }
    }
}
