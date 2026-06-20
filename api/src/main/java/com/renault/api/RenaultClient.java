package com.renault.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.renault.api.kamereon.model.KamereonPersonAccount;
import okhttp3.OkHttpClient;

import java.util.List;

/// Entry point for the Renault API. Authenticates via Gigya and provides access to
/// {@link RenaultAccount} and {@link RenaultVehicle} proxies.
///
/// Quick start:
/// ```java
/// try (var client = new RenaultClient("fr_FR")) {
///     client.login("user@example.com", "password");
///     // Persist the login token to avoid re-authenticating next run:
///     String token = client.getLoginToken();
///
///     var vehicle = client.getAccounts().get(0).getVehicles().get(0);
///     System.out.println(vehicle.getBatteryStatus());
/// }
/// ```
///
/// All calls are synchronous and blocking. Implements {@link AutoCloseable} to cleanly
/// shut down the underlying HTTP connection pool.
public class RenaultClient implements AutoCloseable {
    private final RenaultSession session;
    private final OkHttpClient http;

    /// Creates a client with a default {@link OkHttpClient}.
    ///
    /// @param locale BCP 47-style locale string identifying the country, e.g. `"fr_FR"`.
    ///               See {@link LocaleConfig} for all supported values.
    /// @throws IllegalArgumentException if the locale is not supported
    public RenaultClient(String locale) {
        this(new OkHttpClient(), locale);
    }

    /// Creates a client with a custom {@link OkHttpClient}, useful for configuring
    /// timeouts, proxies, or interceptors.
    ///
    /// @param http   the HTTP client to use for all requests
    /// @param locale BCP 47-style locale string, e.g. `"fr_FR"`
    /// @throws IllegalArgumentException if the locale is not supported
    public RenaultClient(OkHttpClient http, String locale) {
        ObjectMapper mapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .changeDefaultVisibility(v -> v.withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY))
            .build();
        this.http = http;
        this.session = new RenaultSession(http, mapper, locale);
    }

    /// Authenticate with username and password.
    public void login(String loginId, String password) {
        session.login(loginId, password);
    }

    /// Returns the current Gigya login token (store this instead of the password).
    public String getLoginToken() { return session.getLoginToken(); }

    /// Restore a login token from a previous session.
    public void setLoginToken(String token) { session.setLoginToken(token); }

    /// Returns all accounts associated with the authenticated person.
    public List<RenaultAccount> getAccounts() {
        var person = session.getPerson();
        List<KamereonPersonAccount> accounts = person.getAccounts();
        if (accounts == null) return List.of();
        return accounts.stream()
            .map(acc -> new RenaultAccount(acc.accountId(), session))
            .toList();
    }

    /// Returns the account with the given ID.
    public RenaultAccount getAccount(String accountId) {
        return new RenaultAccount(accountId, session);
    }

    /// Returns the underlying session, for advanced use (e.g. accessing raw Kamereon responses).
    public RenaultSession getSession() { return session; }

    @Override
    public void close() {
        http.dispatcher().executorService().shutdown();
        http.connectionPool().evictAll();
    }
}
