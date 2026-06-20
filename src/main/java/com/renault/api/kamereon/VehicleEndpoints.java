package com.renault.api.kamereon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Maps vehicle model codes to their supported endpoints.
 * A null value in a per-model map means that endpoint is unsupported for that model.
 */
public final class VehicleEndpoints {
    private static final Logger log = LoggerFactory.getLogger(VehicleEndpoints.class);

    static final EndpointDefinition DEFAULT_CHARGE_SET_MODE     = kca("/kca/car-adapter/v1/cars/{vin}/actions/charge-mode");
    static final EndpointDefinition DEFAULT_CHARGE_SET_SCHEDULE = kca("/kca/car-adapter/v2/cars/{vin}/actions/charge-schedule");
    static final EndpointDefinition DEFAULT_CHARGE_START        = kca("/kca/car-adapter/v1/cars/{vin}/actions/charging-start");
    static final EndpointDefinition DEFAULT_HORN_START          = kca("/kca/car-adapter/v1/cars/{vin}/actions/horn-lights");
    static final EndpointDefinition DEFAULT_HVAC_SET_SCHEDULE   = kca("/kca/car-adapter/v2/cars/{vin}/actions/hvac-schedule");
    static final EndpointDefinition DEFAULT_HVAC_START          = kca("/kca/car-adapter/v1/cars/{vin}/actions/hvac-start");
    static final EndpointDefinition DEFAULT_LIGHTS_START        = kca("/kca/car-adapter/v1/cars/{vin}/actions/horn-lights");
    static final EndpointDefinition DEFAULT_REFRESH_LOCATION    = kca("/kca/car-adapter/v1/cars/{vin}/actions/refresh-location");
    static final EndpointDefinition DEFAULT_ALERTS              = new EndpointDefinition("/vehicles/{vin}/alerts");
    static final EndpointDefinition DEFAULT_BATTERY_STATUS      = kca("/kca/car-adapter/v2/cars/{vin}/battery-status");
    static final EndpointDefinition DEFAULT_CHARGE_HISTORY      = kca("/kca/car-adapter/v1/cars/{vin}/charge-history");
    static final EndpointDefinition DEFAULT_CHARGE_MODE         = kca("/kca/car-adapter/v1/cars/{vin}/charge-mode");
    static final EndpointDefinition DEFAULT_CHARGE_SCHEDULE     = kca("/kca/car-adapter/v1/cars/{vin}/charge-schedule");
    static final EndpointDefinition DEFAULT_CHARGES             = kca("/kca/car-adapter/v1/cars/{vin}/charges");
    static final EndpointDefinition DEFAULT_CHARGING_SETTINGS   = kca("/kca/car-adapter/v1/cars/{vin}/charging-settings");
    static final EndpointDefinition DEFAULT_COCKPIT             = kca("/kca/car-adapter/v1/cars/{vin}/cockpit");
    static final EndpointDefinition DEFAULT_HVAC_HISTORY        = kca("/kca/car-adapter/v1/cars/{vin}/hvac-history");
    static final EndpointDefinition DEFAULT_HVAC_SESSIONS       = kca("/kca/car-adapter/v1/cars/{vin}/hvac-sessions");
    static final EndpointDefinition DEFAULT_HVAC_SETTINGS       = kca("/kca/car-adapter/v1/cars/{vin}/hvac-settings");
    static final EndpointDefinition DEFAULT_HVAC_STATUS         = kca("/kca/car-adapter/v1/cars/{vin}/hvac-status");
    static final EndpointDefinition DEFAULT_LOCATION            = kca("/kca/car-adapter/v1/cars/{vin}/location");
    static final EndpointDefinition DEFAULT_LOCK_STATUS         = kca("/kca/car-adapter/v1/cars/{vin}/lock-status");
    static final EndpointDefinition DEFAULT_NOTIFICATION_SETTINGS = kca("/kca/car-adapter/v1/cars/{vin}/notification-settings");
    static final EndpointDefinition DEFAULT_PRESSURE            = kca("/kca/car-adapter/v1/cars/{vin}/pressure");
    static final EndpointDefinition DEFAULT_RES_STATE           = kca("/kca/car-adapter/v1/cars/{vin}/res-state");
    static final EndpointDefinition DEFAULT_SOC_LEVELS          = new EndpointDefinition("/kcm/v1/vehicles/{vin}/ev/soc-levels");

    // KCM-specific endpoints
    static final EndpointDefinition KCM_CHARGE_SET_SCHEDULE = new EndpointDefinition("/kcm/v1/vehicles/{vin}/charge/schedule", "kcm");
    static final EndpointDefinition KCM_CHARGE_START        = new EndpointDefinition("/kcm/v1/vehicles/{vin}/charge/start", "kcm");
    static final EndpointDefinition KCM_CHARGE_PAUSE_RESUME = new EndpointDefinition("/kcm/v1/vehicles/{vin}/charge/pause-resume", "kcm-pause-resume");
    static final EndpointDefinition KCM_CHARGE_VIA_SETTINGS = new EndpointDefinition("/kcm/v1/vehicles/{vin}/ev/settings", "kcm-settings");
    static final EndpointDefinition KCA_HVAC_STOP_ALT       = new EndpointDefinition("/kca/car-adapter/v1/cars/{vin}/actions/hvac-start", "kca-stop");

    private static final Map<String, EndpointDefinition> DEFAULT;
    private static final Map<String, Map<String, EndpointDefinition>> VEHICLE_ENDPOINTS;

    static {
        var def = new HashMap<String, EndpointDefinition>();
        def.put("actions/charge-set-mode",     DEFAULT_CHARGE_SET_MODE);
        def.put("actions/charge-set-schedule", DEFAULT_CHARGE_SET_SCHEDULE);
        def.put("actions/charge-start",        DEFAULT_CHARGE_START);
        def.put("actions/charge-stop",         DEFAULT_CHARGE_START); // same path, different action
        def.put("actions/horn-start",          DEFAULT_HORN_START);
        def.put("actions/hvac-set-schedule",   DEFAULT_HVAC_SET_SCHEDULE);
        def.put("actions/hvac-start",          DEFAULT_HVAC_START);
        def.put("actions/hvac-stop",           DEFAULT_HVAC_START);  // same path, different action
        def.put("actions/lights-start",        DEFAULT_LIGHTS_START);
        def.put("actions/refresh-location",    DEFAULT_REFRESH_LOCATION);
        def.put("alerts",                      DEFAULT_ALERTS);
        def.put("battery-status",              DEFAULT_BATTERY_STATUS);
        def.put("charge-history",              DEFAULT_CHARGE_HISTORY);
        def.put("charge-mode",                 DEFAULT_CHARGE_MODE);
        def.put("charge-schedule",             DEFAULT_CHARGE_SCHEDULE);
        def.put("charges",                     DEFAULT_CHARGES);
        def.put("charging-settings",           DEFAULT_CHARGING_SETTINGS);
        def.put("cockpit",                     DEFAULT_COCKPIT);
        def.put("hvac-history",                DEFAULT_HVAC_HISTORY);
        def.put("hvac-sessions",               DEFAULT_HVAC_SESSIONS);
        def.put("hvac-settings",               DEFAULT_HVAC_SETTINGS);
        def.put("hvac-status",                 DEFAULT_HVAC_STATUS);
        def.put("location",                    DEFAULT_LOCATION);
        def.put("lock-status",                 DEFAULT_LOCK_STATUS);
        def.put("notification-settings",       DEFAULT_NOTIFICATION_SETTINGS);
        def.put("pressure",                    DEFAULT_PRESSURE);
        def.put("res-state",                   DEFAULT_RES_STATE);
        def.put("soc-levels",                  DEFAULT_SOC_LEVELS);
        DEFAULT = Collections.unmodifiableMap(def);

        var m = new HashMap<String, Map<String, EndpointDefinition>>();

        m.put("A4E1VE", build()  // Renault R4 E-Tech
            .n("actions/charge-set-mode").n("actions/charge-set-schedule")
            .ep("actions/charge-start", KCM_CHARGE_VIA_SETTINGS).n("actions/charge-stop")
            .d("actions/horn-start").n("actions/hvac-set-schedule")
            .d("actions/hvac-start").d("actions/hvac-stop").d("actions/lights-start")
            .d("battery-status").n("charge-history").n("charge-mode")
            .ep("charge-schedule", KCM_CHARGE_VIA_SETTINGS)
            .d("charges").d("cockpit").d("hvac-status").d("location")
            .n("lock-status").n("notification-settings").n("pressure").n("res-state")
            .d("soc-levels").done());

        m.put("A5E1AE", build()  // Alpine A290
            .n("actions/charge-start").n("actions/charge-stop")
            .d("actions/horn-start").d("actions/hvac-start")
            .ep("actions/hvac-stop", KCA_HVAC_STOP_ALT)
            .d("actions/lights-start").d("battery-status")
            .n("charge-history").n("charge-mode")
            .ep("charge-schedule", KCM_CHARGE_VIA_SETTINGS)
            .d("charges").d("cockpit").d("hvac-settings").d("hvac-status").d("location")
            .n("lock-status").n("pressure").n("res-state").d("soc-levels").done());

        m.put("DU31SU", build()  // Dacia Duster III
            .n("actions/horn-start").n("actions/lights-start")
            .n("battery-status").n("charge-history").n("charge-mode")
            .n("charge-schedule").n("charges").n("charging-settings")
            .d("cockpit").n("hvac-history").n("hvac-sessions")
            .n("hvac-settings").n("hvac-status").d("location")
            .n("lock-status").n("pressure").n("res-state").n("soc-levels").done());

        m.put("R5E1VE", build()  // Renault 5 E-TECH
            .n("actions/charge-set-mode").n("actions/charge-set-schedule")
            .ep("actions/charge-start", KCM_CHARGE_VIA_SETTINGS).n("actions/charge-stop")
            .d("actions/horn-start").n("actions/hvac-set-schedule").d("actions/hvac-start")
            .ep("actions/hvac-stop", KCA_HVAC_STOP_ALT)
            .d("actions/lights-start").d("actions/refresh-location").d("alerts")
            .d("battery-status").n("charge-history").n("charge-mode")
            .ep("charge-schedule", KCM_CHARGE_VIA_SETTINGS)
            .d("charges").n("charging-settings").d("cockpit")
            .n("hvac-history").n("hvac-sessions").d("hvac-settings").d("hvac-status")
            .d("location").n("lock-status").n("notification-settings")
            .n("pressure").n("res-state").d("soc-levels").done());

        m.put("X071VE", build()  // TWINGO III
            .d("battery-status").n("charge-history").n("charge-mode")
            .n("charge-schedule").d("charging-settings").d("cockpit")
            .n("hvac-history").n("hvac-sessions").d("hvac-settings")
            .d("hvac-status").d("location").n("lock-status")
            .n("notification-settings").n("pressure").n("res-state").done());

        m.put("X101VE", build()  // ZOE phase 1
            .d("actions/charge-set-schedule").d("actions/charge-start")
            .d("actions/hvac-start").d("actions/hvac-stop")
            .d("battery-status").d("charge-mode").d("charge-schedule")
            .d("cockpit").d("hvac-status")
            .n("location").n("lock-status").n("pressure").n("res-state").n("soc-levels").done());

        m.put("X102VE", build()  // ZOE phase 2
            .d("actions/charge-start")
            .ep("actions/charge-stop", KCM_CHARGE_PAUSE_RESUME)
            .n("actions/horn-start").d("actions/hvac-start").n("actions/lights-start")
            .d("battery-status").n("charge-mode").n("charge-schedule")
            .d("charging-settings").d("cockpit").d("hvac-settings").d("hvac-status")
            .d("location").n("lock-status").d("pressure").n("res-state").n("soc-levels").done());

        m.put("XBG1VE", build()  // DACIA SPRING
            .ep("actions/charge-start", KCM_CHARGE_PAUSE_RESUME)
            .ep("actions/charge-stop",  KCM_CHARGE_PAUSE_RESUME)
            .n("actions/horn-start").d("actions/hvac-start")
            .ep("actions/hvac-stop", KCA_HVAC_STOP_ALT)
            .n("actions/lights-start").d("actions/refresh-location")
            .n("alerts").d("battery-status").n("charge-history")
            .n("charge-mode").n("charge-schedule").n("charging-settings")
            .d("cockpit").n("hvac-history").n("hvac-sessions")
            .n("hvac-settings").d("hvac-status").d("location")
            .n("lock-status").n("notification-settings").n("pressure")
            .n("res-state").n("soc-levels").done());

        m.put("XCB1SE", build()  // SCENIC E-TECH
            .d("battery-status").n("charge-mode")
            .ep("charge-schedule", KCM_CHARGE_VIA_SETTINGS)
            .d("cockpit").d("hvac-settings").d("hvac-status")
            .d("location").n("lock-status").n("res-state").done());

        m.put("XCB1VE", build()  // MEGANE E-TECH
            .ep("actions/charge-set-schedule", KCM_CHARGE_SET_SCHEDULE)
            .ep("actions/charge-start", KCM_CHARGE_START)
            .n("actions/charge-stop").d("actions/horn-start")
            .d("actions/hvac-start").d("actions/lights-start")
            .d("battery-status").n("charge-history").n("charge-mode")
            .n("charge-schedule").d("charging-settings").d("cockpit")
            .n("hvac-history").n("hvac-sessions").d("hvac-settings").d("hvac-status")
            .d("location").n("lock-status").n("notification-settings")
            .n("pressure").n("res-state").d("soc-levels").done());

        m.put("XDD1VE", build()  // Renault Master E-Tech
            .d("actions/charge-set-mode").n("actions/charge-set-schedule")
            .ep("actions/charge-start", KCM_CHARGE_VIA_SETTINGS).n("actions/charge-stop")
            .d("actions/horn-start").d("actions/hvac-start").d("actions/hvac-stop")
            .d("actions/lights-start").d("battery-status").n("charge-history").n("charge-mode")
            .ep("charge-schedule", KCM_CHARGE_VIA_SETTINGS)
            .n("charging-settings").d("cockpit").n("hvac-history")
            .n("hvac-sessions").d("hvac-settings").d("hvac-status")
            .d("location").n("lock-status").n("notification-settings")
            .n("pressure").n("res-state").d("soc-levels").done());

        m.put("XFB2BI", build()  // Megane IV
            .d("battery-status").n("charge-history").n("charge-mode")
            .n("charge-schedule").d("charging-settings").d("cockpit")
            .n("hvac-history").n("hvac-sessions").d("hvac-settings")
            .d("hvac-status").n("location").n("lock-status")
            .n("notification-settings").n("pressure").n("res-state").done());

        m.put("XHN1CP", build()  // Rafale
            .n("actions/charge-start").d("actions/horn-start").d("actions/lights-start")
            .d("battery-status").n("charge-history").n("charge-mode")
            .ep("charge-schedule", KCM_CHARGE_VIA_SETTINGS)
            .n("charging-settings").d("cockpit").n("hvac-history")
            .n("hvac-sessions").d("hvac-settings").d("hvac-status")
            .d("location").n("lock-status").n("notification-settings")
            .n("pressure").n("res-state").done());

        m.put("XHN1ML", build()  // Renault Espace VI
            .d("actions/horn-start").n("actions/hvac-start").d("actions/lights-start")
            .n("battery-status").n("charge-history").n("charge-mode")
            .n("charge-schedule").n("charges").n("charging-settings")
            .d("cockpit").n("hvac-history").n("hvac-sessions")
            .n("hvac-settings").n("hvac-status").d("location")
            .n("lock-status").n("notification-settings").n("pressure")
            .n("res-state").n("soc-levels").done());

        m.put("XHN1SU", build()  // AUSTRAL
            .n("actions/charge-start").n("actions/charge-stop")
            .d("actions/horn-start").n("actions/hvac-start").d("actions/lights-start")
            .n("battery-status").n("charge-history").n("charge-mode").n("charging-settings")
            .d("cockpit").n("hvac-status").d("location")
            .n("lock-status").n("pressure").n("res-state").n("soc-levels").done());

        m.put("XJA1VP", build()  // CLIO V (variant 1)
            .n("actions/charge-start").n("actions/charge-stop").n("actions/horn-start")
            .n("actions/hvac-start").n("actions/lights-start").n("alerts")
            .n("battery-status").n("charge-history").n("charge-mode").n("charge-schedule")
            .n("charges").n("charging-settings").d("cockpit")
            .n("hvac-history").n("hvac-sessions").n("hvac-settings").n("hvac-status")
            .d("location").n("lock-status").n("notification-settings")
            .n("pressure").n("res-state").n("soc-levels").done());

        m.put("XJA2VP", build()  // CLIO V (variant 2)
            .n("actions/charge-start").n("actions/charge-stop").n("actions/horn-start")
            .n("actions/hvac-start").n("actions/lights-start").n("alerts")
            .n("battery-status").n("charge-history").n("charge-mode").n("charge-schedule")
            .n("charges").n("charging-settings").d("cockpit")
            .n("hvac-history").n("hvac-sessions").n("hvac-settings").n("hvac-status")
            .d("location").n("lock-status").n("notification-settings")
            .n("pressure").n("res-state").n("soc-levels").done());

        m.put("XJB1SU", build()  // CAPTUR II
            .n("actions/charge-start").n("actions/charge-stop").n("actions/horn-start")
            .d("actions/hvac-start").n("actions/lights-start").d("battery-status")
            .n("charge-history").n("charge-mode").n("charge-schedule")
            .d("charging-settings").d("cockpit").n("hvac-history").n("hvac-sessions")
            .d("hvac-settings").d("hvac-status").d("location")
            .n("lock-status").n("notification-settings").n("pressure")
            .n("res-state").n("soc-levels").done());

        m.put("XJB2CP", build()  // Renault Symbioz 2025
            .n("actions/charge-start").n("actions/charge-stop").d("actions/horn-start")
            .n("actions/hvac-start").d("actions/lights-start").n("battery-status")
            .n("charge-mode").n("charging-settings").d("cockpit")
            .n("hvac-status").d("location").n("lock-status").n("pressure").n("res-state").done());

        m.put("XJF2BI", build()  // DACIA SANDERO III
            .n("actions/charge-set-mode").n("actions/charge-set-schedule")
            .n("actions/charge-start").n("actions/charge-stop").d("actions/horn-start")
            .n("actions/hvac-set-schedule").n("actions/hvac-start").n("actions/hvac-stop")
            .d("actions/lights-start").d("actions/refresh-location")
            .n("alerts").n("battery-status").n("charge-history").n("charge-mode")
            .n("charge-schedule").n("charges").n("charging-settings")
            .d("cockpit").n("hvac-history").n("hvac-sessions").n("hvac-settings")
            .n("hvac-status").d("location").n("lock-status").n("notification-settings")
            .n("pressure").n("res-state").n("soc-levels").done());

        m.put("XJL2TR", build()  // Arkana E-tech full hybrid
            .n("charge-history").n("charge-mode").n("charge-schedule")
            .n("charging-settings").d("cockpit").n("hvac-history")
            .n("hvac-sessions").n("hvac-settings").n("hvac-status")
            .d("location").n("lock-status").n("pressure").n("res-state").done());

        VEHICLE_ENDPOINTS = Collections.unmodifiableMap(m);
    }

    // ---- Vehicle specifications ----

    private static final Map<String, Map<String, Boolean>> VEHICLE_SPECS = Map.of(
        "X101VE", Map.of(
            "reports-charge-session-durations-in-minutes", true,
            "reports-in-watts", true
        ),
        "XBG1VE", Map.of("control-charge-via-kcm", true)
    );

    public static boolean getSpecification(String modelCode, String key) {
        if (modelCode == null) return false;
        var specs = VEHICLE_SPECS.get(modelCode);
        return specs != null && Boolean.TRUE.equals(specs.get(key));
    }

    // ---- Public API ----

    private static final Set<String> warnedModels = Collections.synchronizedSet(new HashSet<>());
    private static final Set<String> warnedEndpoints = Collections.synchronizedSet(new HashSet<>());

    public static Map<String, EndpointDefinition> getModelEndpoints(String modelCode) {
        if (modelCode == null) return DEFAULT;
        if (!VEHICLE_ENDPOINTS.containsKey(modelCode)) {
            if (warnedModels.add(modelCode)) {
                log.warn("Model {} is not documented; using default endpoints. "
                    + "See https://github.com/hacf-fr/renault-api/issues/1747", modelCode);
            }
            return DEFAULT;
        }
        return VEHICLE_ENDPOINTS.get(modelCode);
    }

    /**
     * Returns the EndpointDefinition for the endpoint on this model, or null if unsupported.
     * If the endpoint is not listed for the model, falls back to the default.
     */
    public static EndpointDefinition getModelEndpoint(String modelCode, String endpoint) {
        var endpoints = getModelEndpoints(modelCode);
        if (!endpoints.containsKey(endpoint)) {
            String key = modelCode + ":" + endpoint;
            if (warnedEndpoints.add(key)) {
                log.warn("Endpoint {} for model {} is not documented; using default. "
                    + "See https://github.com/hacf-fr/renault-api/issues/1747", endpoint, modelCode);
            }
            return DEFAULT.get(endpoint);
        }
        return endpoints.get(endpoint);
    }

    // ---- Builder ----

    private static Builder build() { return new Builder(); }

    private static final class Builder {
        private final HashMap<String, EndpointDefinition> map = new HashMap<>();

        Builder n(String key) { map.put(key, null); return this; }
        Builder d(String key) { map.put(key, DEFAULT.get(key)); return this; }
        Builder ep(String key, EndpointDefinition def) { map.put(key, def); return this; }
        Map<String, EndpointDefinition> done() { return Collections.unmodifiableMap(map); }
    }

    private static EndpointDefinition kca(String path) { return new EndpointDefinition(path); }

    private VehicleEndpoints() {}
}
