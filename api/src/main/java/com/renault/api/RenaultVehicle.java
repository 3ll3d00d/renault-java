package com.renault.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

/** Proxy to a single Renault vehicle. All methods are synchronous. */
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

    public KamereonVehicleDetails getDetails() {
        if (cachedDetails != null) return cachedDetails;
        cachedDetails = session.getVehicleDetails(accountId, vin);
        return cachedDetails;
    }

    public CarAdapterData getCarAdapter() {
        if (cachedCarAdapter != null) return cachedCarAdapter;
        var response = getData(CAR_ADAPTER_ENDPOINT);
        cachedCarAdapter = response.getAttributes(CarAdapterData.class);
        return cachedCarAdapter;
    }

    public List<KamereonVehicleContract> getContracts() {
        if (cachedContracts != null) return cachedContracts;
        var response = session.getVehicleContracts(accountId, vin);
        cachedContracts = response.getContractList() != null ? response.getContractList() : List.of();
        return cachedContracts;
    }

    public boolean supportsEndpoint(String endpoint) {
        return getDetails().supportsEndpoint(endpoint);
    }

    // ---- Read endpoints ----

    public BatteryStatusData getBatteryStatus() {
        return getData(ep("battery-status")).getAttributes(BatteryStatusData.class);
    }

    public BatterySocData getBatterySoc() {
        return getData(ep("soc-levels")).getAttributes(BatterySocData.class);
    }

    public CockpitData getCockpit() {
        return getData(ep("cockpit")).getAttributes(CockpitData.class);
    }

    public LocationData getLocation() {
        return getData(ep("location")).getAttributes(LocationData.class);
    }

    public LockStatusData getLockStatus() {
        return getData(ep("lock-status")).getAttributes(LockStatusData.class);
    }

    public HvacStatusData getHvacStatus() {
        return getData(ep("hvac-status")).getAttributes(HvacStatusData.class);
    }

    public HvacSettingsData getHvacSettings() {
        return getData(ep("hvac-settings")).getAttributes(HvacSettingsData.class);
    }

    public ChargeModeData getChargeMode() {
        return getData(ep("charge-mode")).getAttributes(ChargeModeData.class);
    }

    public ChargingSettingsData getChargingSettings() {
        return getData(ep("charging-settings")).getAttributes(ChargingSettingsData.class);
    }

    public TyrePressureData getTyrePressure() {
        return getData(ep("pressure")).getAttributes(TyrePressureData.class);
    }

    public ResStateData getResState() {
        return getData(ep("res-state")).getAttributes(ResStateData.class);
    }

    /** Returns raw JSON attributes for charge-schedule (structure varies by vehicle). */
    public JsonNode getChargeSchedule() {
        return getData(ep("charge-schedule")).getRawAttributes();
    }

    public JsonNode getChargeHistory(LocalDate start, LocalDate end, String period) {
        if (!"day".equals(period) && !"month".equals(period)) {
            throw new IllegalArgumentException("`period` must be 'day' or 'month'");
        }
        DateTimeFormatter fmt = "day".equals(period) ? DAY_FORMAT : MONTH_FORMAT;
        Map<String, String> params = Map.of("type", period, "start", start.format(fmt), "end", end.format(fmt));
        return getData(ep("charge-history"), params).getRawAttributes();
    }

    public JsonNode getCharges(LocalDate start, LocalDate end) {
        Map<String, String> params = Map.of("start", start.format(DAY_FORMAT), "end", end.format(DAY_FORMAT));
        return getData(ep("charges"), params).getRawAttributes();
    }

    public JsonNode getHvacHistory(LocalDate start, LocalDate end, String period) {
        if (!"day".equals(period) && !"month".equals(period)) {
            throw new IllegalArgumentException("`period` must be 'day' or 'month'");
        }
        DateTimeFormatter fmt = "day".equals(period) ? DAY_FORMAT : MONTH_FORMAT;
        Map<String, String> params = Map.of("type", period, "start", start.format(fmt), "end", end.format(fmt));
        return getData(ep("hvac-history"), params).getRawAttributes();
    }

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

    public KamereonVehicleDataResponse startAc(double temperature) {
        return startAc(temperature, null);
    }

    public KamereonVehicleDataResponse startAc(double temperature, ZonedDateTime when) {
        var attrs = new HashMap<String, Object>();
        attrs.put("action", "start");
        attrs.put("targetTemperature", temperature);
        if (when != null) {
            attrs.put("startDateTime", when.withZoneSameInstant(ZoneOffset.UTC).format(TZ_FORMAT));
        }
        return setAction(ep("actions/hvac-start"), actionBody("HvacStart", attrs));
    }

    public KamereonVehicleDataResponse stopAc() {
        var endpoint = ep("actions/hvac-stop");
        String action = "kca-stop".equals(endpoint.mode()) ? "stop" : "cancel";
        return setAction(endpoint, actionBody("HvacStart", Map.of("action", action)));
    }

    public KamereonVehicleDataResponse setHvacSchedules(List<HvacSchedule> schedules) {
        var body = actionBody("HvacSchedule", Map.of(
            "schedules", schedules.stream().map(HvacSchedule::forJson).toList()
        ));
        return setAction(ep("actions/hvac-set-schedule"), body);
    }

    public KamereonVehicleDataResponse setChargeMode(String mode) {
        return setAction(ep("actions/charge-set-mode"), actionBody("ChargeMode", Map.of("action", mode)));
    }

    public KamereonVehicleDataResponse startCharging() {
        return startCharging(null);
    }

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

    public KamereonVehicleDataResponse stopCharging() {
        var endpoint = ep("actions/charge-stop");
        var body = "kcm-pause-resume".equals(endpoint.mode())
            ? actionBody("ChargePauseResume", Map.of("action", "pause"))
            : actionBody("ChargingStart", Map.of("action", "stop"));
        return setAction(endpoint, body);
    }

    public KamereonVehicleDataResponse setChargeSchedules(List<ChargeSchedule> schedules) {
        var body = actionBody("ChargeSchedule", Map.of(
            "schedules", schedules.stream().map(ChargeSchedule::forJson).toList()
        ));
        return setAction(ep("actions/charge-set-schedule"), body);
    }

    public KamereonVehicleDataResponse startHorn() {
        return setAction(ep("actions/horn-start"), actionBody("HornLights", Map.of("action", "start", "target", "horn")));
    }

    public KamereonVehicleDataResponse startLights() {
        return setAction(ep("actions/lights-start"), actionBody("HornLights", Map.of("action", "start", "target", "lights")));
    }

    public KamereonVehicleDataResponse refreshLocation() {
        return setAction(ep("actions/refresh-location"), Map.of("data", Map.of("type", "RefreshLocation")));
    }

    public KamereonVehicleDataResponse setBatterySoc(int min, int target) {
        return setAction(ep("soc-levels"), Map.of("socMin", min, "socTarget", target));
    }

    // ---- Internals ----

    /** Resolve a named endpoint for this vehicle's model, throwing if unsupported. */
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
