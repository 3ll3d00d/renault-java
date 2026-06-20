# renault-java

A Java port of the Python library [hacf-fr/renault-api](https://github.com/hacf-fr/renault-api), which provides access to Renault's Kamereon vehicle API (as used by the MyRenault app).

## Status

Direct port — behaviour is kept identical to the Python library. The upstream baseline this port tracks is recorded in [`upstream-baseline.txt`](upstream-baseline.txt).

## Requirements

- Java 25
- Gradle (use the included wrapper: `./gradlew`)

## Project structure

```
renault-java/
  api/       — the library (published artifact)
  harness/   — integration test harness for verifying against a real vehicle
```

## Building

```bash
./gradlew build           # compile and test all subprojects
./gradlew :api:test       # run library tests only
./gradlew :api:javadoc    # generate API documentation
```

## Usage

```java
try (var client = new RenaultClient("fr_FR")) {
    client.login("user@example.com", "password");

    // Persist the login token to avoid re-authenticating next run:
    String token = client.getLoginToken();

    var vehicle = client.getAccounts().get(0).getVehicles().get(0);
    System.out.println(vehicle.getVin());

    var battery = vehicle.getBatteryStatus();
    System.out.println(battery.batteryLevel() + "% — " + battery.batteryAutonomy() + " km range");

    var location = vehicle.getLocation();
    System.out.println(location.gpsLatitude() + ", " + location.gpsLongitude());
}
```

Restoring a saved login token (avoids re-authenticating on every run):

```java
try (var client = new RenaultClient("fr_FR")) {
    client.setLoginToken(savedToken);
    // proceed as above
}
```

### Supported operations

**Read endpoints** (via `RenaultVehicle`):
`getBatteryStatus`, `getBatterySoc`, `getCockpit`, `getLocation`, `getLockStatus`,
`getHvacStatus`, `getHvacSettings`, `getChargeMode`, `getChargingSettings`,
`getTyrePressure`, `getResState`, `getChargeSchedule`, `getChargeHistory`,
`getCharges`, `getHvacHistory`, `getHvacSessions`, `getNotificationSettings`, `getAlerts`

**Action endpoints**:
`startAc`, `stopAc`, `setHvacSchedules`, `setChargeMode`, `startCharging`,
`stopCharging`, `setChargeSchedules`, `startHorn`, `startLights`, `refreshLocation`, `setBatterySoc`

## Integration test harness

The `harness/` subproject is a developer tool for verifying the library against a real vehicle. It calls live read endpoints, shows the raw HTTP exchange, and compares responses structurally against the test fixtures.

**CLI** (interactive menu):
```bash
export RENAULT_USER=you@example.com
export RENAULT_PASS=your-password
export RENAULT_LOCALE=fr_FR
./gradlew :harness:run
```

**Browser UI** (side-by-side live JSON vs fixture JSON with diff table):
```bash
./gradlew :harness:run -PmainClass=com.renault.harness.HarnessServer
# open http://localhost:8080
```

The structural diff highlights fields that are missing, extra, or have a different type compared to the nearest fixture file — useful for validating that a new vehicle model's responses match the expected schema.

To add a new vehicle model's fixture: copy the live JSON printed by the harness into a file under `api/src/test/resources/fixtures/kamereon/vehicle_data/` and add a `@Test` in `KamereonVehicleDataTest`.

## What is Kamereon?

Kamereon is Renault's connected-vehicle platform. This library authenticates via Gigya (SAP CDC) and then calls the Kamereon REST API to read vehicle telemetry (battery, location, HVAC status, etc.) and send commands (start charging, set charge schedule, pre-condition cabin temperature, etc.).

## Supported vehicles

Any Renault, Dacia, or Alpine vehicle that works with MyRenault. Per-model endpoint variations are handled by `VehicleEndpoints` — the `harness` tool is the recommended way to capture and validate behaviour for models not yet covered by the fixture set.

## Licence

MIT — see [LICENSE](LICENSE).

This project is a port of [hacf-fr/renault-api](https://github.com/hacf-fr/renault-api) (© 2020 epenet), used and distributed under the terms of the MIT licence. Test fixtures are taken directly from the upstream repository.
