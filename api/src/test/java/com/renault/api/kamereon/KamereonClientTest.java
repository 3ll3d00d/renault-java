package com.renault.api.kamereon;

import com.renault.api.TestFixtures;
import com.renault.api.exception.KamereonException;
import com.renault.api.kamereon.model.KamereonVehicleDataResponse;
import com.renault.api.kamereon.model.KamereonVehiclesResponse;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/kamereon/test_kamereon.py — MockWebServer for request verification. */
class KamereonClientTest {
    private static final String API_KEY = "test-api-key";
    private static final String JWT = "test-jwt-token";

    private MockWebServer server;
    private KamereonClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        baseUrl = server.url("").toString().replaceAll("/$", "");
        client = new KamereonClient(new OkHttpClient(), TestFixtures.MAPPER);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    private MockResponse kamereonResponse(String body) {
        return new MockResponse()
            .setBody(body)
            .addHeader("Content-Type", "application/vnd.api+json");
    }

    private MockResponse kamereonFixture(String path) {
        return kamereonResponse(TestFixtures.load(path));
    }

    // ---- Request construction helpers ----

    private RecordedRequest nextRequest() throws InterruptedException {
        return server.takeRequest();
    }

    private void assertCommonHeaders(RecordedRequest req) {
        assertEquals(API_KEY, req.getHeader("apikey"));
        assertEquals(JWT, req.getHeader("x-gigya-id_token"));
        // OkHttp may append "; charset=utf-8" for POST bodies, so check prefix only
        String ct = req.getHeader("Content-type");
        assertNotNull(ct, "Content-type header must be present");
        assertTrue(ct.startsWith("application/vnd.api+json"),
            "Content-type must be application/vnd.api+json, got: " + ct);
    }

    private void assertQueryParam(RecordedRequest req, String key, String value) {
        String url = req.getRequestUrl().toString();
        String expected = key + "=" + value;
        assertTrue(url.contains(expected), "Expected query param '%s' in URL: %s".formatted(expected, url));
    }

    // ---- getPerson ----

    @Test
    void testGetPersonUrl() throws Exception {
        server.enqueue(kamereonResponse("{\"accounts\":[]}"));
        client.getPerson(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_PERSON_ID);

        RecordedRequest req = nextRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().startsWith("/commerce/v1/persons/" + TestFixtures.TEST_PERSON_ID),
            "Expected persons path, got: " + req.getPath());
        assertQueryParam(req, "country", "FR");
        assertCommonHeaders(req);
    }

    // ---- getAccountVehicles ----

    @Test
    void testGetAccountVehiclesUrl() throws Exception {
        server.enqueue(kamereonFixture(TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicles/zoe_40.1.json"));
        var result = client.getAccountVehicles(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_ACCOUNT_ID);

        RecordedRequest req = nextRequest();
        assertEquals("GET", req.getMethod());
        assertTrue(req.getPath().startsWith("/commerce/v1/accounts/" + TestFixtures.TEST_ACCOUNT_ID + "/vehicles"),
            "Expected vehicles path, got: " + req.getPath());
        assertQueryParam(req, "country", "FR");
        assertCommonHeaders(req);

        assertNotNull(result.getVehicleLinks());
        assertEquals(1, result.getVehicleLinks().size());
        assertEquals(TestFixtures.TEST_VIN, result.getVehicleLinks().get(0).vin());
    }

    // ---- getVehicleDetails ----

    @Test
    void testGetVehicleDetailsUrl() throws Exception {
        server.enqueue(kamereonFixture(TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_details/zoe_40.1.json"));
        var result = client.getVehicleDetails(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_ACCOUNT_ID, TestFixtures.TEST_VIN);

        RecordedRequest req = nextRequest();
        assertEquals("GET", req.getMethod());
        String expectedPath = "/commerce/v1/accounts/" + TestFixtures.TEST_ACCOUNT_ID + "/vehicles/" + TestFixtures.TEST_VIN + "/details";
        assertTrue(req.getPath().startsWith(expectedPath),
            "Expected details path, got: " + req.getPath());
        assertQueryParam(req, "country", "FR");
        assertCommonHeaders(req);

        assertNotNull(result);
        assertEquals("X101VE", result.getModelCode());
    }

    // ---- getVehicleData ----

    @Test
    void testGetVehicleDataUrl() throws Exception {
        server.enqueue(kamereonFixture(TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_data/battery-status.1.json"));
        var endpoint = new EndpointDefinition("/kca/car-adapter/v2/cars/{vin}/battery-status");
        var result = client.getVehicleData(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_ACCOUNT_ID, TestFixtures.TEST_VIN, endpoint, Map.of());

        RecordedRequest req = nextRequest();
        assertEquals("GET", req.getMethod());
        String expectedPath = "/commerce/v1/accounts/" + TestFixtures.TEST_ACCOUNT_ID
            + "/kamereon/kca/car-adapter/v2/cars/" + TestFixtures.TEST_VIN + "/battery-status";
        assertTrue(req.getPath().startsWith(expectedPath),
            "Expected battery-status path, got: " + req.getPath());
        assertQueryParam(req, "country", "FR");
        assertCommonHeaders(req);

        assertNotNull(result);
        result.raiseForErrorCode();
    }

    @Test
    void testGetVehicleDataExtraParams() throws Exception {
        server.enqueue(kamereonFixture(TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_data/charge-mode.json"));
        var endpoint = new EndpointDefinition("/kca/car-adapter/v1/cars/{vin}/charge-mode");
        client.getVehicleData(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_ACCOUNT_ID, TestFixtures.TEST_VIN,
            endpoint, Map.of("type", "usage"));

        RecordedRequest req = nextRequest();
        assertQueryParam(req, "country", "FR");
        assertQueryParam(req, "type", "usage");
    }

    // ---- setVehicleAction ----

    @Test
    void testSetVehicleActionUrl() throws Exception {
        server.enqueue(kamereonFixture(TestFixtures.KAMEREON_FIXTURE_PATH + "/vehicle_action/hvac-start.start.json"));
        var endpoint = new EndpointDefinition("/kca/car-adapter/v1/cars/{vin}/actions/hvac-start");
        var body = Map.of("data", Map.of("type", "HvacStart", "attributes", Map.of("action", "start", "targetTemperature", 21)));
        client.setVehicleAction(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_ACCOUNT_ID, TestFixtures.TEST_VIN, endpoint, body);

        RecordedRequest req = nextRequest();
        assertEquals("POST", req.getMethod());
        String expectedPath = "/commerce/v1/accounts/" + TestFixtures.TEST_ACCOUNT_ID
            + "/kamereon/kca/car-adapter/v1/cars/" + TestFixtures.TEST_VIN + "/actions/hvac-start";
        assertTrue(req.getPath().startsWith(expectedPath),
            "Expected hvac-start action path, got: " + req.getPath());
        assertQueryParam(req, "country", "FR");
        assertCommonHeaders(req);

        String requestBody = req.getBody().readUtf8();
        assertTrue(requestBody.contains("HvacStart"), "Expected body to contain action type");
    }

    // ---- buildVehicleUrl ----

    @Test
    void testBuildVehicleUrlReplacesVin() {
        String url = KamereonClient.buildVehicleUrl(
            "https://api.example.com",
            "account-id-1",
            "VF1AAAAA555777999",
            "/kca/car-adapter/v2/cars/{vin}/battery-status"
        );
        assertEquals(
            "https://api.example.com/commerce/v1/accounts/account-id-1/kamereon/kca/car-adapter/v2/cars/VF1AAAAA555777999/battery-status",
            url
        );
    }

    // ---- error handling ----

    @Test
    void testKamereonErrorResponse() {
        server.enqueue(kamereonFixture(TestFixtures.KAMEREON_FIXTURE_PATH + "/error/not_supported.json"));
        var endpoint = new EndpointDefinition("/kca/car-adapter/v1/cars/{vin}/hvac-status");

        var ex = assertThrows(KamereonException.class, () ->
            client.getVehicleData(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_ACCOUNT_ID, TestFixtures.TEST_VIN, endpoint, Map.of()));

        assertNotNull(ex.getErrorCode());
        assertNotNull(ex.getErrorDetails());
    }

    @Test
    void testHttpErrorResponse() {
        server.enqueue(new MockResponse().setResponseCode(403).setBody("Forbidden"));
        var endpoint = new EndpointDefinition("/kca/car-adapter/v1/cars/{vin}/battery-status");

        assertThrows(KamereonException.class, () ->
            client.getVehicleData(baseUrl, API_KEY, JWT, "FR", TestFixtures.TEST_ACCOUNT_ID, TestFixtures.TEST_VIN, endpoint, Map.of()));
    }
}
