package com.renault.api;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import com.renault.api.exception.EndpointNotAvailableException;
import com.renault.api.kamereon.EndpointDefinition;
import com.renault.api.kamereon.VehicleEndpoints;
import com.renault.api.kamereon.model.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Proxy to a single Renault vehicle. All methods make synchronous HTTP calls.
///
/// Instances are obtained via {@link RenaultAccount#getVehicles()} or
/// {@link RenaultAccount#getVehicle(String)}. Vehicle details and contracts are
/// fetched lazily and cached for the lifetime of this instance.
///
/// Not all endpoints are available on every vehicle model. Methods that call an
/// unsupported endpoint throw {@link com.renault.api.exception.EndpointNotAvailableException}.
/// Use {@link #supportsEndpoint(String)} to check availability before calling.
public class RenaultVehicle {
    private static final DateTimeFormatter DAY_FORMAT   = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter TZ_FORMAT    = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    // Endpoint used when "" is requested (car adapter base)
    private static final EndpointDefinition CAR_ADAPTER_ENDPOINT =
        new EndpointDefinition("/kca/car-adapter/v2/cars/{vin}");

    private final String accountId;
    private final String vin;
    private final RenaultSession session;

    private volatile KamereonVehicleDetails cachedDetails;
    private volatile CarAdapterData cachedCarAdapter;
    private volatile List<KamereonVehicleContract> cachedContracts;

    RenaultVehicle(String accountId, String vin, RenaultSession session, KamereonVehicleDetails details) {
        this.accountId = accountId;
        this.vin = vin;
        this.session = session;
        this.cachedDetails = details;
    }

    public String getAccountId() { return accountId; }
    public String getVin() { return vin; }

    // ---- Vehicle metadata ----

    /// Returns static details for this vehicle (brand, model, VIN, energy type). Cached after first call.
    public KamereonVehicleDetails getDetails() {
        if (cachedDetails != null) return cachedDetails;
        cachedDetails = session.getVehicleDetails(accountId, vin);
        return cachedDetails;
    }

    /// Returns car-adapter data (connectivity, charging mode support flags). Cached after first call.
    public CarAdapterData getCarAdapter() {
        if (cachedCarAdapter != null) return cachedCarAdapter;
        var response = getData(CAR_ADAPTER_ENDPOINT);
        cachedCarAdapter = response.getAttributes(CarAdapterData.class);
        return cachedCarAdapter;
    }

    /// Returns active contracts (connected services, warranty) for this vehicle. Cached after first call.
    public List<KamereonVehicleContract> getContracts() {
        if (cachedContracts != null) return cachedContracts;
        var response = session.getVehicleContracts(accountId, vin);
        cachedContracts = response.getContractList() != null ? response.getContractList() : List.of();
        return cachedContracts;
    }

    /// Returns `true` if this vehicle model supports the named endpoint.
    /// Endpoint names match the keys used in `VehicleEndpoints.json`, e.g.
    /// `"battery-status"`, `"location"`, `"actions/hvac-start"`.
    public boolean supportsEndpoint(String endpoint) {
        return getDetails().supportsEndpoint(endpoint);
    }

    // ---- Read endpoints ----

    /// Returns battery state: charge level (%), estimated range (km), plug and charging status.
    public BatteryStatusData getBatteryStatus() {
        return getData(ep("battery-status")).getAttributes(BatteryStatusData.class);
    }

    /// Returns state-of-charge thresholds (minimum and target SoC percentages).
    public BatterySocData getBatterySoc() {
        return getData(ep("soc-levels")).getAttributes(BatterySocData.class);
    }

    /// Returns odometer and fuel readings (total mileage in km, fuel autonomy in km, fuel quantity in L).
    public CockpitData getCockpit() {
        return getData(ep("cockpit")).getAttributes(CockpitData.class);
    }

    /// Returns last known GPS position (latitude, longitude, heading in degrees).
    public LocationData getLocation() {
        return getData(ep("location")).getAttributes(LocationData.class);
    }

    /// Returns current door/lock state.
    public LockStatusData getLockStatus() {
        return getData(ep("lock-status")).getAttributes(LockStatusData.class);
    }

    /// Returns current HVAC (climate control) status and cabin temperature.
    public HvacStatusData getHvacStatus() {
        return getData(ep("hvac-status")).getAttributes(HvacStatusData.class);
    }

    /// Returns HVAC pre-conditioning schedules and default target temperature.
    public HvacSettingsData getHvacSettings() {
        return getData(ep("hvac-settings")).getAttributes(HvacSettingsData.class);
    }

    /// Returns current charge mode (`"always"`, `"schedule_mode"`, or `"plug_in"` depending on model).
    public ChargeModeData getChargeMode() {
        return getData(ep("charge-mode")).getAttributes(ChargeModeData.class);
    }

    /// Returns charging schedule configuration (programs, power limits).
    public ChargingSettingsData getChargingSettings() {
        return getData(ep("charging-settings")).getAttributes(ChargingSettingsData.class);
    }

    /// Returns tyre pressure readings for all four wheels.
    public TyrePressureData getTyrePressure() {
        return getData(ep("pressure")).getAttributes(TyrePressureData.class);
    }

    /// Returns remote electric service (RES) state (vehicle connectivity readiness).
    public ResStateData getResState() {
        return getData(ep("res-state")).getAttributes(ResStateData.class);
    }

    /// Returns raw JSON attributes for charge-schedule (structure varies by vehicle).
    public JsonNode getChargeSchedule() {
        return getData(ep("charge-schedule")).getRawAttributes();
    }

    /// Returns aggregated charge history between `start` and `end` (inclusive).
    ///
    /// @param period `"day"` for per-day breakdown or `"month"` for per-month
    /// @throws IllegalArgumentException if `period` is not `"day"` or `"month"`
    public JsonNode getChargeHistory(LocalDate start, LocalDate end, String period) {
        if (!"day".equals(period) && !"month".equals(period)) {
            throw new IllegalArgumentException("`period` must be 'day' or 'month'");
        }
        DateTimeFormatter fmt = "day".equals(period) ? DAY_FORMAT : MONTH_FORMAT;
        Map<String, String> params = Map.of("type", period, "start", start.format(fmt), "end", end.format(fmt));
        return getData(ep("charge-history"), params).getRawAttributes();
    }

    /// Returns individual charge sessions between `start` and `end` (inclusive).
    public JsonNode getCharges(LocalDate start, LocalDate end) {
        Map<String, String> params = Map.of("start", start.format(DAY_FORMAT), "end", end.format(DAY_FORMAT));
        return getData(ep("charges"), params).getRawAttributes();
    }

    /// Returns aggregated HVAC (pre-conditioning) history between `start` and `end` (inclusive).
    ///
    /// @param period `"day"` for per-day breakdown or `"month"` for per-month
    /// @throws IllegalArgumentException if `period` is not `"day"` or `"month"`
    public JsonNode getHvacHistory(LocalDate start, LocalDate end, String period) {
        if (!"day".equals(period) && !"month".equals(period)) {
            throw new IllegalArgumentException("`period` must be 'day' or 'month'");
        }
        DateTimeFormatter fmt = "day".equals(period) ? DAY_FORMAT : MONTH_FORMAT;
        Map<String, String> params = Map.of("type", period, "start", start.format(fmt), "end", end.format(fmt));
        return getData(ep("hvac-history"), params).getRawAttributes();
    }

    /// Returns individual HVAC pre-conditioning sessions between `start` and `end` (inclusive).
    public JsonNode getHvacSessions(LocalDate start, LocalDate end) {
        Map<String, String> params = Map.of("start", start.format(DAY_FORMAT), "end", end.format(DAY_FORMAT));
        return getData(ep("hvac-sessions"), params).getRawAttributes();
    }

    public JsonNode getNotificationSettings() {
        return getData(ep("notification-settings")).getRawAttributes();
    }

    public JsonNode getAlerts() {
        return getData(ep("alerts")).getRawAttributes();
    }

    // ---- Actions ----

    /// Starts the air conditioning immediately.
    ///
    /// @param temperature target cabin temperature in degrees Celsius
    public KamereonVehicleDataResponse startAc(double temperature) {
        return startAc(temperature, null);
    }

    /// Schedules the air conditioning to start at a specific time.
    ///
    /// @param temperature target cabin temperature in degrees Celsius
    /// @param when        UTC time to start; pass `null` to start immediately
    public KamereonVehicleDataResponse startAc(double temperature, ZonedDateTime when) {
        var attrs = new HashMap<String, Object>();
        attrs.put("action", "start");
        attrs.put("targetTemperature", temperature);
        if (when != null) {
            attrs.put("startDateTime", when.withZoneSameInstant(ZoneOffset.UTC).format(TZ_FORMAT));
        }
        return setAction(ep("actions/hvac-start"), actionBody("HvacStart", attrs));
    }

    /// Stops or cancels a running or scheduled HVAC session.
    public KamereonVehicleDataResponse stopAc() {
        var endpoint = ep("actions/hvac-stop");
        String action = "kca-stop".equals(endpoint.mode()) ? "stop" : "cancel";
        return setAction(endpoint, actionBody("HvacStart", Map.of("action", action)));
    }

    /// Updates the HVAC pre-conditioning weekly schedule.
    ///
    /// @param schedules list of {@link HvacSchedule} programs; each schedule slot specifies
    ///                  a `readyAtTime` per day in `"THH:mmZ"` format
    public KamereonVehicleDataResponse setHvacSchedules(List<HvacSchedule> schedules) {
        var body = actionBody("HvacSchedule", Map.of(
            "schedules", schedules.stream().map(HvacSchedule::forJson).toList()
        ));
        return setAction(ep("actions/hvac-set-schedule"), body);
    }

    /// Sets the charge mode.
    ///
    /// @param mode `"always"` to charge whenever plugged in, or `"schedule_mode"`
    ///             to use the configured schedule
    public KamereonVehicleDataResponse setChargeMode(String mode) {
        return setAction(ep("actions/charge-set-mode"), actionBody("ChargeMode", Map.of("action", mode)));
    }

    /// Starts charging immediately. Behaviour varies by model (may disable scheduled programs).
    public KamereonVehicleDataResponse startCharging() {
        return startCharging(null);
    }

    /// Starts or schedules charging.
    ///
    /// @param when UTC time to begin charging; pass `null` to start immediately
    public KamereonVehicleDataResponse startCharging(ZonedDateTime when) {
        var endpoint = ep("actions/charge-start");
        Object body = switch (endpoint.mode()) {
            case "kcm-settings" -> {
                // GET current settings, disable all scheduled programs, POST back
                var current = getData(endpoint).getRawAttributes();
                if (current != null && current.has("programs")) {
                    current.get("programs").forEach(p -> {
                        if (p instanceof ObjectNode on) on.put("programActivationStatus", false);
                    });
                }
                yield current;
            }
            case "kcm-pause-resume" ->
                actionBody("ChargePauseResume", Map.of("action", "resume"));
            default -> {
                var attrs = new HashMap<String, Object>();
                attrs.put("action", "start");
                if (when != null) attrs.put("startDateTime", when.withZoneSameInstant(ZoneOffset.UTC).format(TZ_FORMAT));
                yield actionBody("ChargingStart", attrs);
            }
        };
        return setAction(endpoint, body);
    }

    /// Pauses or stops an active charging session.
    public KamereonVehicleDataResponse stopCharging() {
        var endpoint = ep("actions/charge-stop");
        var body = "kcm-pause-resume".equals(endpoint.mode())
            ? actionBody("ChargePauseResume", Map.of("action", "pause"))
            : actionBody("ChargingStart", Map.of("action", "stop"));
        return setAction(endpoint, body);
    }

    /// Updates the weekly charging schedule.
    ///
    /// @param schedules list of {@link ChargeSchedule} programs; each slot specifies
    ///                  a `startTime` in `"THH:mmZ"` format and a `duration` in minutes
    public KamereonVehicleDataResponse setChargeSchedules(List<ChargeSchedule> schedules) {
        var body = actionBody("ChargeSchedule", Map.of(
            "schedules", schedules.stream().map(ChargeSchedule::forJson).toList()
        ));
        return setAction(ep("actions/charge-set-schedule"), body);
    }

    /// Sounds the horn briefly to help locate the vehicle.
    public KamereonVehicleDataResponse startHorn() {
        return setAction(ep("actions/horn-start"), actionBody("HornLights", Map.of("action", "start", "target", "horn")));
    }

    /// Flashes the lights briefly to help locate the vehicle.
    public KamereonVehicleDataResponse startLights() {
        return setAction(ep("actions/lights-start"), actionBody("HornLights", Map.of("action", "start", "target", "lights")));
    }

    /// Requests the vehicle to push an updated GPS location to Kamereon.
    public KamereonVehicleDataResponse refreshLocation() {
        return setAction(ep("actions/refresh-location"), Map.of("data", Map.of("type", "RefreshLocation")));
    }

    /// Sets the state-of-charge thresholds.
    ///
    /// @param min    minimum SoC percentage (charge will not discharge below this via V2L)
    /// @param target target SoC percentage to charge up to
    public KamereonVehicleDataResponse setBatterySoc(int min, int target) {
        return setAction(ep("soc-levels"), Map.of("socMin", min, "socTarget", target));
    }

    // ---- Internals ----

    /// Resolve a named endpoint for this vehicle's model, throwing if unsupported.
    private EndpointDefinition ep(String name) {
        var details = getDetails();
        var def = details.getEndpoint(name);
        if (def == null) throw new EndpointNotAvailableException(name, details.getModelCode());
        return def;
    }

    private KamereonVehicleDataResponse getData(EndpointDefinition endpoint) {
        return getData(endpoint, null);
    }

    private KamereonVehicleDataResponse getData(EndpointDefinition endpoint, Map<String, String> params) {
        return session.getVehicleData(accountId, vin, endpoint, params);
    }

    private KamereonVehicleDataResponse setAction(EndpointDefinition endpoint, Object body) {
        return session.setVehicleAction(accountId, vin, endpoint, body);
    }

    private static Map<String, Object> actionBody(String type, Map<String, Object> attributes) {
        return Map.of("data", Map.of("type", type, "attributes", attributes));
    }
}
