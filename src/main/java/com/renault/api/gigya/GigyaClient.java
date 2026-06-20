package com.renault.api.gigya;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.api.exception.GigyaException;
import com.renault.api.gigya.model.GigyaBaseResponse;
import com.renault.api.gigya.model.GigyaAccountInfoResponse;
import com.renault.api.gigya.model.GigyaJwtResponse;
import com.renault.api.gigya.model.GigyaLoginResponse;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GigyaClient {
    private static final Logger log = LoggerFactory.getLogger(GigyaClient.class);

    private final OkHttpClient http;
    private final ObjectMapper mapper;

    public GigyaClient(OkHttpClient http, ObjectMapper mapper) {
        this.http = http;
        this.mapper = mapper;
    }

    public GigyaLoginResponse login(String rootUrl, String apiKey, String loginId, String password) {
        var body = new FormBody.Builder()
            .add("ApiKey", apiKey)
            .add("loginID", loginId)
            .add("password", password)
            .build();
        return post(rootUrl + "/accounts.login", body, GigyaLoginResponse.class);
    }

    public GigyaAccountInfoResponse getAccountInfo(String rootUrl, String apiKey, String loginToken) {
        var body = new FormBody.Builder()
            .add("ApiKey", apiKey)
            .add("login_token", loginToken)
            .build();
        return post(rootUrl + "/accounts.getAccountInfo", body, GigyaAccountInfoResponse.class);
    }

    public GigyaJwtResponse getJwt(String rootUrl, String apiKey, String loginToken) {
        var body = new FormBody.Builder()
            .add("ApiKey", apiKey)
            .add("login_token", loginToken)
            .add("fields", "data.personId,data.gigyaDataCenter")
            .add("expiration", "900")
            .build();
        return post(rootUrl + "/accounts.getJWT", body, GigyaJwtResponse.class);
    }

    private <T extends GigyaBaseResponse> T post(String url, FormBody body, Class<T> type) {
        var request = new Request.Builder().url(url).post(body).build();
        String responseText;
        try (Response response = http.newCall(request).execute()) {
            responseText = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful() && responseText.isEmpty()) {
                response.close();
                throw new GigyaException("HTTP %d from Gigya".formatted(response.code()));
            }
        } catch (IOException e) {
            throw new GigyaException("Gigya request failed: " + e.getMessage(), e);
        }
        try {
            T parsed = mapper.readValue(responseText, type);
            parsed.raiseForErrorCode();
            return parsed;
        } catch (IOException e) {
            throw new GigyaException("Gigya responded with invalid JSON: " + e.getMessage(), e);
        }
    }
}
