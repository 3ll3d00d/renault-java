package com.renault.api;

import tools.jackson.databind.ObjectMapper;
import com.renault.api.exception.GigyaException;
import com.renault.api.exception.NotAuthenticatedException;
import com.renault.api.gigya.GigyaClient;
import com.renault.api.kamereon.EndpointDefinition;
import com.renault.api.kamereon.KamereonClient;
import com.renault.api.kamereon.model.KamereonPersonResponse;
import com.renault.api.kamereon.model.KamereonVehicleContractsResponse;
import com.renault.api.kamereon.model.KamereonVehicleDataResponse;
import com.renault.api.kamereon.model.KamereonVehicleDetails;
import com.renault.api.kamereon.model.KamereonVehiclesResponse;
import okhttp3.OkHttpClient;

import java.time.Instant;
import java.util.Map;

/// Manages authentication state and delegates to Gigya + Kamereon clients.
/// JWT tokens are cached until expiry. Login tokens survive across sessions.
public class RenaultSession {
    private static final int JWT_EXPIRY_SECONDS = 900;
    private static final int JWT_REFRESH_MARGIN_SECONDS = 60;

    private final GigyaClient gigya;
    private final KamereonClient kamereon;
    private final LocaleConfig.Credentials credentials;
    private final String locale;

    private volatile String loginToken;
    private volatile String personId;

    private volatile String jwt;
    private volatile Instant jwtExpiry = Instant.EPOCH;
    private final Object jwtLock = new Object();

    public RenaultSession(OkHttpClient http, ObjectMapper mapper, String locale) {
        this.credentials = LocaleConfig.requireForLocale(locale);
        this.locale = locale;
        this.gigya = new GigyaClient(http, mapper);
        this.kamereon = new KamereonClient(http, mapper);
    }

    /// Create a session with explicit credentials (skip locale lookup).
    public RenaultSession(OkHttpClient http, ObjectMapper mapper, LocaleConfig.Credentials credentials, String locale) {
        this.credentials = credentials;
        this.locale = locale;
        this.gigya = new GigyaClient(http, mapper);
        this.kamereon = new KamereonClient(http, mapper);
    }

    // ---- Auth ----

    /// Authenticate with username/password. Stores the login token for subsequent calls.
    public void login(String loginId, String password) {
        clearAuthState();
        var response = gigya.login(credentials.gigyaUrl(), credentials.gigyaApiKey(), loginId, password);
        this.loginToken = response.getSessionCookie();
    }

    /// Returns the current login token. Store this securely instead of the password.
    /// Restore it with {@link #setLoginToken} on the next session.
    public String getLoginToken() { return loginToken; }

    /// Restores a login token from a previous session, avoiding re-authentication.
    public void setLoginToken(String token) {
        clearAuthState();
        this.loginToken = token;
    }

    private void clearAuthState() {
        loginToken = null;
        personId = null;
        synchronized (jwtLock) {
            jwt = null;
            jwtExpiry = Instant.EPOCH;
        }
    }

    private String requireLoginToken() {
        if (loginToken == null) throw new NotAuthenticatedException("Not authenticated. Call login() or setLoginToken() first.");
        return loginToken;
    }

    private String resolvePersonId() {
        if (personId != null) return personId;
        var response = gigya.getAccountInfo(credentials.gigyaUrl(), credentials.gigyaApiKey(), requireLoginToken());
        personId = response.getPersonId();
        return personId;
    }

    String resolveJwt() {
        synchronized (jwtLock) {
            if (jwt != null && Instant.now().isBefore(jwtExpiry.minusSeconds(JWT_REFRESH_MARGIN_SECONDS))) {
                return jwt;
            }
            try {
                var response = gigya.getJwt(credentials.gigyaUrl(), credentials.gigyaApiKey(), requireLoginToken());
                jwt = response.getJwt();
                jwtExpiry = Instant.now().plusSeconds(JWT_EXPIRY_SECONDS);
                return jwt;
            } catch (GigyaException e) {
                // 403005 / 403013 = token expired; clear and rethrow as NotAuthenticated
                if (e.getErrorCode() == 403005 || e.getErrorCode() == 403013) {
                    clearAuthState();
                    throw new NotAuthenticatedException("Authentication expired: " + e.getMessage());
                }
                throw e;
            }
        }
    }

    // ---- Delegating Kamereon calls ----

    public KamereonPersonResponse getPerson() {
        return kamereon.getPerson(credentials.kamereonUrl(), credentials.kamereonApiKey(),
            resolveJwt(), credentials.country(), resolvePersonId());
    }

    public KamereonVehiclesResponse getAccountVehicles(String accountId) {
        return kamereon.getAccountVehicles(credentials.kamereonUrl(), credentials.kamereonApiKey(),
            resolveJwt(), credentials.country(), accountId);
    }

    public KamereonVehicleDetails getVehicleDetails(String accountId, String vin) {
        return kamereon.getVehicleDetails(credentials.kamereonUrl(), credentials.kamereonApiKey(),
            resolveJwt(), credentials.country(), accountId, vin);
    }

    public KamereonVehicleContractsResponse getVehicleContracts(String accountId, String vin) {
        return kamereon.getVehicleContracts(credentials.kamereonUrl(), credentials.kamereonApiKey(),
            resolveJwt(), credentials.country(), locale, accountId, vin);
    }

    public KamereonVehicleDataResponse getVehicleData(String accountId, String vin, EndpointDefinition endpoint, Map<String, String> params) {
        return kamereon.getVehicleData(credentials.kamereonUrl(), credentials.kamereonApiKey(),
            resolveJwt(), credentials.country(), accountId, vin, endpoint, params);
    }

    public KamereonVehicleDataResponse setVehicleAction(String accountId, String vin, EndpointDefinition endpoint, Object body) {
        return kamereon.setVehicleAction(credentials.kamereonUrl(), credentials.kamereonApiKey(),
            resolveJwt(), credentials.country(), accountId, vin, endpoint, body);
    }

    public String getCountry() { return credentials.country(); }
    public String getLocale() { return locale; }
    KamereonClient kamereonClient() { return kamereon; }
}
