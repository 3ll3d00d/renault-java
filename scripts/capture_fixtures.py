#!/usr/bin/env python3
"""
Capture live Kamereon vehicle-data responses as JSON fixture files
for the renault-java test suite.

The script authenticates, calls every supported read endpoint, and writes
the raw API response for each into:
    api/src/test/resources/fixtures/kamereon/vehicle_data/<endpoint>.<vin>.json

These files are consumed directly by KamereonVehicleDataTest and the harness
StructuralDiff, so the naming convention must match.

Requirements:
    pip install renault-api aiohttp

Environment variables:
    RENAULT_USER          required — account email
    RENAULT_PASS          required — account password
    RENAULT_LOCALE        optional — BCP 47 locale, default fr_FR
    RENAULT_FIXTURE_PATH  optional — output directory, default as above
"""
import asyncio
import json
import os
import re
import sys
from pathlib import Path

try:
    import aiohttp
    from renault_api.renault_client import RenaultClient
except ImportError:
    sys.exit("Missing dependencies — run:  pip install renault-api aiohttp")


# Maps the URL path fragment returned by the Kamereon API to the fixture
# file prefix used by the Java test suite.
ENDPOINT_MAP: dict[str, str] = {
    "battery-status":         "battery-status",
    "battery-inhibit-sensor": "battery-soc",
    "cockpit":                "cockpit",
    "hvac-status":            "hvac-status",
    "hvac-settings":          "hvac-settings",
    "charge-mode":            "charge-mode",
    "charging-settings":      "charging-settings",
    "location":               "location",
    "lock-status":            "lock-status",
    "res-state":              "res-state",
    "tyre-pressure":          "pressure",
    "alert-notifications":    "alerts",
    "notification-settings":  "notification-settings",
    "charge-history":         "charge-history",
    "charges":                "charges",
    "hvac-history":           "hvac-history",
    "hvac-sessions":          "hvac-sessions",
}

# High-level RenaultVehicle calls to trigger each endpoint.
# Each entry is (label, async-callable(vehicle)).
# Failures are caught and reported without aborting the rest.
VEHICLE_CALLS = [
    ("battery-status",     lambda v: v.get_battery_status()),
    ("battery-soc",        lambda v: v.get_battery_soc()),
    ("cockpit",            lambda v: v.get_cockpit()),
    ("hvac-status",        lambda v: v.get_hvac_status()),
    ("hvac-settings",      lambda v: v.get_hvac_settings()),
    ("charge-mode",        lambda v: v.get_charge_mode()),
    ("charging-settings",  lambda v: v.get_charging_settings()),
    ("location",           lambda v: v.get_location()),
    ("lock-status",        lambda v: v.get_lock_status()),
    ("res-state",          lambda v: v.get_res_state()),
    ("pressure",           lambda v: v.get_tyre_pressure()),
    ("alerts",             lambda v: v.get_alerts()),
    ("notification-settings", lambda v: v.get_notification_settings()),
    ("charge-history",     lambda v: v.get_charge_history()),
    ("charges",            lambda v: v.get_charges()),
    ("hvac-history",       lambda v: v.get_hvac_history()),
    ("hvac-sessions",      lambda v: v.get_hvac_sessions()),
]


async def main() -> None:
    user     = os.environ.get("RENAULT_USER")
    password = os.environ.get("RENAULT_PASS")
    if not user or not password:
        sys.exit("RENAULT_USER and RENAULT_PASS must be set")
    locale      = os.environ.get("RENAULT_LOCALE", "fr_FR")
    fixture_dir = Path(os.environ.get(
        "RENAULT_FIXTURE_PATH",
        "api/src/test/resources/fixtures/kamereon/vehicle_data",
    ))
    fixture_dir.mkdir(parents=True, exist_ok=True)

    # Raw response bytes captured per fixture name.
    # Populated inside the trace callbacks below.
    captured: dict[str, bytes] = {}

    # aiohttp fires on_request_end after the library has read and buffered
    # the response body (renault-api calls response.json() before releasing
    # the context, so response.read() here returns the already-buffered bytes).
    async def on_request_end(
        session: aiohttp.ClientSession,
        ctx: object,
        params: aiohttp.TraceRequestEndParams,
    ) -> None:
        if params.method.upper() != "GET":
            return
        url = str(params.response.url)
        if "/kca/car-adapter/" not in url:
            return
        try:
            body = await params.response.read()
        except Exception:
            return
        if not body:
            return
        path = params.response.url.path
        for fragment, name in ENDPOINT_MAP.items():
            if f"/{fragment}" in path:
                captured[name] = body
                return

    trace = aiohttp.TraceConfig()
    trace.on_request_end.append(on_request_end)

    async with aiohttp.ClientSession(trace_configs=[trace]) as session:
        client = RenaultClient(websession=session, locale=locale)
        await client.session.login(user, password)
        print(f"Logged in as {user}")

        accounts = await client.get_api_accounts()
        if not accounts:
            sys.exit("No accounts found")
        vehicles = await accounts[0].get_api_vehicles()
        if not vehicles:
            sys.exit("No vehicles found")
        vehicle = vehicles[0]
        vin     = vehicle.details.vin
        print(f"Vehicle: {vin}\n")

        for label, call in VEHICLE_CALLS:
            before = len(captured)
            try:
                await call(vehicle)
                ok = len(captured) > before
                print(f"  {'✓' if ok else '? (not captured)'} {label}")
            except Exception as exc:
                print(f"  ✗ {label}: {exc}")

    if not captured:
        sys.exit(
            "\nNothing was captured. Possible causes:\n"
            "  • URL pattern doesn't match — check ENDPOINT_MAP\n"
            "  • response.read() returned empty — renault-api may buffer differently\n"
        )

    # Suffix with first 8 chars of sanitised VIN, e.g. VF1AAAAA555777999 → vf1aaaaa
    vin_slug = re.sub(r"[^a-z0-9]", "", vin.lower())[:8]
    print(f"\nWriting to {fixture_dir}/")
    written = 0
    for name, raw in sorted(captured.items()):
        try:
            pretty = json.dumps(json.loads(raw), indent=2)
        except json.JSONDecodeError as exc:
            print(f"  ! {name}: invalid JSON ({exc}) — skipping")
            continue
        out = fixture_dir / f"{name}.{vin_slug}.json"
        out.write_text(pretty + "\n")
        print(f"  {out.name}")
        written += 1

    print(f"\n{written} fixture files written.")
    print(
        f"\nNext steps:\n"
        f"  1. Add @Test methods in KamereonVehicleDataTest referencing the new files\n"
        f"  2. Run: ./gradlew :api:test\n"
        f"  3. If you want structural comparison in the harness, files are already in place\n"
    )


asyncio.run(main())
