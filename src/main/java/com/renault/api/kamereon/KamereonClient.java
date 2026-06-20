package com.renault.api.kamereon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.api.exception.KamereonException;
import com.renault.api.exception.RenaultException;
import com.renault.api.kamereon.model.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class KamereonClient {
    private static final Logger log = LoggerFactory.getLogger(KamereonClient.class);
    private static final MediaType JSON = MediaType.get("application/vnd.api+json");

    private final OkHttpClient http;
    private final ObjectMapper mapper;

    public KamereonClient(OkHttpClient http, ObjectMapper mapper) {
        this.http = http;
        this.mapper = mapper;
    }

    // ---- Person / account ----

    public KamereonPersonResponse getPerson(String rootUrl, String apiKey, String jwt, String country, String personId) {
        String url = "%s/commerce/v1/persons/%s".formatted(rootUrl, personId);
        return get(url, apiKey, jwt, Map.of("country", country), KamereonPersonResponse.class);
    }

    public KamereonVehiclesResponse getAccountVehicles(String rootUrl, String apiKey, String jwt, String country, String accountId) {
        String url = "%s/commerce/v1/accounts/%s/vehicles".formatted(rootUrl, accountId);
        return get(url, apiKey, jwt, Map.of("country", country), KamereonVehiclesResponse.class);
    }

    public KamereonVehicleDetailsResponse getVehicleDetails(String rootUrl, String apiKey, String jwt, String country, String accountId, String vin) {
        String url = "%s/commerce/v1/accounts/%s/vehicles/%s/details".formatted(rootUrl, accountId, vin);
        return get(url, apiKey, jwt, Map.of("country", country), KamereonVehicleDetailsResponse.class);
    }

    public KamereonVehicleContractsResponse getVehicleContracts(String rootUrl, String apiKey, String jwt, String country, String locale, String accountId, String vin) {
        String url = "%s/commerce/v1/accounts/%s/vehicles/%s/contracts".formatted(rootUrl, accountId, vin);
        var params = Map.of(
            "country", country,
            "locale", locale,
            "brand", "RENAULT",
            "connectedServicesContracts", "true",
            "warranty", "true",
            "warrantyMaintenanceContracts", "true"
        );
        return getWrapped(url, apiKey, jwt, params, "contractList", KamereonVehicleContractsResponse.class);
    }

    // ---- Vehicle data (GET) ----

    public KamereonVehicleDataResponse getVehicleData(String rootUrl, String apiKey, String jwt, String country, String accountId, String vin, EndpointDefinition endpoint, Map<String, String> extraParams) {
        String url = buildVehicleUrl(rootUrl, accountId, vin, endpoint.endpoint());
        var params = new java.util.HashMap<>(extraParams != null ? extraParams : Map.of());
        params.put("country", country);
        return get(url, apiKey, jwt, params, KamereonVehicleDataResponse.class);
    }

    // ---- Vehicle actions (POST) ----

    public KamereonVehicleDataResponse setVehicleAction(String rootUrl, String apiKey, String jwt, String country, String accountId, String vin, EndpointDefinition endpoint, Object body) {
        String url = buildVehicleUrl(rootUrl, accountId, vin, endpoint.endpoint());
        return post(url, apiKey, jwt, Map.of("country", country), body, KamereonVehicleDataResponse.class);
    }

    // ---- URL building ----

    /** Replace {vin} placeholder in path and prepend account root. */
    public static String buildVehicleUrl(String rootUrl, String accountId, String vin, String endpointPath) {
        String path = endpointPath.replace("{vin}", vin);
        return "%s/commerce/v1/accounts/%s/kamereon%s".formatted(rootUrl, accountId, path);
    }

    // ---- HTTP helpers ----

    private <T extends KamereonResponse> T get(String url, String apiKey, String jwt, Map<String, ?> params, Class<T> type) {
        Request request = buildRequest(url, apiKey, jwt, params).get().build();
        return execute(request, type, null);
    }

    private <T extends KamereonResponse> T getWrapped(String url, String apiKey, String jwt, Map<String, ?> params, String wrapKey, Class<T> type) {
        Request request = buildRequest(url, apiKey, jwt, params).get().build();
        return execute(request, type, wrapKey);
    }

    private <T extends KamereonResponse> T post(String url, String apiKey, String jwt, Map<String, ?> params, Object body, Class<T> type) {
        String json;
        try {
            json = mapper.writeValueAsString(body);
        } catch (IOException e) {
            throw new RenaultException("Failed to serialize request body: " + e.getMessage(), e);
        }
        log.debug("POST {} body: {}", url, json);
        Request request = buildRequest(url, apiKey, jwt, params)
            .post(RequestBody.create(json, JSON))
            .build();
        return execute(request, type, null);
    }

    private Request.Builder buildRequest(String url, String apiKey, String jwt, Map<String, ?> params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        params.forEach((k, v) -> urlBuilder.addQueryParameter(k, String.valueOf(v)));
        return new Request.Builder()
            .url(urlBuilder.build())
            .header("Content-type", "application/vnd.api+json")
            .header("apikey", apiKey)
            .header("x-gigya-id_token", jwt);
    }

    private <T extends KamereonResponse> T execute(Request request, Class<T> type, String wrapKey) {
        String responseText;
        int statusCode;
        try (Response response = http.newCall(request).execute()) {
            statusCode = response.code();
            responseText = response.body() != null ? response.body().string() : "";
        } catch (IOException e) {
            throw new RenaultException("Kamereon request failed: " + e.getMessage(), e);
        }
        log.debug("Kamereon {} {} -> {}: {}", request.method(), request.url(), statusCode, responseText);

        if (responseText.startsWith("[")) {
            String key = wrapKey != null ? wrapKey : "data";
            responseText = "{\"" + key + "\": " + responseText + "}";
        }

        if (!responseText.startsWith("{")) {
            if (statusCode >= 400) {
                throw new KamereonException(String.valueOf(statusCode), "HTTP error " + statusCode);
            }
            throw new KamereonException("INVALID_JSON", "Kamereon returned non-JSON: " + responseText);
        }

        T parsed;
        try {
            parsed = mapper.readValue(responseText, type);
        } catch (IOException e) {
            throw new RenaultException("Failed to parse Kamereon response: " + e.getMessage(), e);
        }

        parsed.raiseForErrorCode();

        if (statusCode >= 400) {
            throw new KamereonException(String.valueOf(statusCode), "HTTP error " + statusCode);
        }

        return parsed;
    }
}
