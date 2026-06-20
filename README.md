# renault-java

A Java port of the Python library [hacf-fr/renault-api](https://github.com/hacf-fr/renault-api), which provides access to Renault's Kamereon vehicle API (as used by the MyRenault app).

## Status

Direct port — behaviour is kept identical to the Python library. The upstream baseline this port tracks is recorded in [`upstream-baseline.txt`](upstream-baseline.txt).

## Requirements

- Java 21+
- Gradle (use the included wrapper: `./gradlew`)

## Building

```bash
./gradlew build          # compile and test
./gradlew test           # run tests only
```

## Usage

```java
var session = new RenaultSession(
    gigyaApiKey, kamereonApiKey, rootUrl, gigyaUrl, country, locale);
session.login(username, password);

var client = new RenaultClient(session);
for (var account : client.getAccounts()) {
    for (var vehicle : account.getVehicles()) {
        var battery = vehicle.getBatteryStatus();
        System.out.println(vehicle.getVin() + ": " + battery.getBatteryLevel() + "%");
    }
}
```

## What is Kamereon?

Kamereon is Renault's connected-vehicle platform. This library authenticates via Gigya (SAP CDC) and then calls the Kamereon REST API to read vehicle telemetry (battery, location, HVAC status, etc.) and send commands (start charging, set charge schedule, pre-condition cabin temperature, etc.).

## Supported vehicles

Any Renault, Dacia, or Alpine vehicle that works with MyRenault. Per-model endpoint variations (which features are available for which model) are handled by `VehicleEndpoints`.

## Licence

MIT — see [LICENSE](LICENSE).

This project is a port of [hacf-fr/renault-api](https://github.com/hacf-fr/renault-api) (© 2020 epenet), used and distributed under the terms of the MIT licence. Test fixtures are taken directly from the upstream repository.
