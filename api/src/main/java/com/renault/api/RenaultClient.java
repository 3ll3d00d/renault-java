package com.renault.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.api.kamereon.model.KamereonPersonAccount;
import okhttp3.OkHttpClient;

import java.util.List;

/**
 * Entry point. Authenticates with Gigya and provides access to account/vehicle proxies.
 */
public class RenaultClient implements AutoCloseable {
    private final RenaultSession session;
    private final OkHttpClient http;

    public RenaultClient(String locale) {
        this(new OkHttpClient(), locale);
    }

    public RenaultClient(OkHttpClient http, String locale) {
        ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.http = http;
        this.session = new RenaultSession(http, mapper, locale);
    }

    /** Authenticate with username and password. */
    public void login(String loginId, String password) {
        session.login(loginId, password);
    }

    /** Returns the current Gigya login token (store this instead of the password). */
    public String getLoginToken() { return session.getLoginToken(); }

    /** Restore a login token from a previous session. */
    public void setLoginToken(String token) { session.setLoginToken(token); }

    /** Returns all accounts associated with the authenticated person. */
    public List<RenaultAccount> getAccounts() {
        var person = session.getPerson();
        List<KamereonPersonAccount> accounts = person.getAccounts();
        if (accounts == null) return List.of();
        return accounts.stream()
            .map(acc -> new RenaultAccount(acc.accountId(), session))
            .toList();
    }

    /** Returns the account with the given ID. */
    public RenaultAccount getAccount(String accountId) {
        return new RenaultAccount(accountId, session);
    }

    public RenaultSession getSession() { return session; }

    @Override
    public void close() {
        http.dispatcher().executorService().shutdown();
        http.connectionPool().evictAll();
    }
}
