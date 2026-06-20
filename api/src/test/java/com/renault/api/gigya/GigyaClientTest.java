package com.renault.api.gigya;

import com.renault.api.TestFixtures;
import com.renault.api.exception.GigyaException;
import com.renault.api.exception.InvalidCredentialsException;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/gigya/test_gigya.py — uses MockWebServer instead of aiointercept. */
class GigyaClientTest {
    private MockWebServer server;
    private GigyaClient client;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        baseUrl = server.url("").toString().replaceAll("/$", "");
        client = new GigyaClient(new OkHttpClient(), TestFixtures.MAPPER);
    }

    @AfterEach
    void tearDown() throws Exception {
        server.shutdown();
    }

    private MockResponse gigyaResponse(String fixturePath) {
        return new MockResponse()
            .setBody(TestFixtures.load(fixturePath))
            .addHeader("Content-Type", "text/javascript");
    }

    @Test
    void testLogin() throws Exception {
        server.enqueue(gigyaResponse(TestFixtures.GIGYA_FIXTURE_PATH + "/login.json"));
        var response = client.login(baseUrl, "test-api-key", TestFixtures.TEST_USERNAME, TestFixtures.TEST_PASSWORD);
        assertEquals(TestFixtures.TEST_LOGIN_TOKEN, response.getSessionCookie());

        RecordedRequest req = server.takeRequest();
        assertEquals("/accounts.login", req.getPath());
        String body = req.getBody().readUtf8();
        assertTrue(body.contains("loginID=" + TestFixtures.TEST_USERNAME.replace("@", "%40")));
    }

    @Test
    void testLoginError() {
        server.enqueue(gigyaResponse(TestFixtures.GIGYA_FIXTURE_PATH + "/error/login.403042.json"));
        assertThrows(InvalidCredentialsException.class, () ->
            client.login(baseUrl, "test-api-key", TestFixtures.TEST_USERNAME, "wrong-password"));
    }

    @Test
    void testGetAccountInfo() throws Exception {
        server.enqueue(gigyaResponse(TestFixtures.GIGYA_FIXTURE_PATH + "/get_account_info.json"));
        var response = client.getAccountInfo(baseUrl, "test-api-key", TestFixtures.TEST_LOGIN_TOKEN);
        assertEquals(TestFixtures.TEST_PERSON_ID, response.getPersonId());

        RecordedRequest req = server.takeRequest();
        assertEquals("/accounts.getAccountInfo", req.getPath());
    }

    @Test
    void testGetJwt() throws Exception {
        server.enqueue(gigyaResponse(TestFixtures.GIGYA_FIXTURE_PATH + "/get_jwt.json"));
        var response = client.getJwt(baseUrl, "test-api-key", TestFixtures.TEST_LOGIN_TOKEN);
        assertNotNull(response.getJwt());
        assertFalse(response.getJwt().isBlank());

        RecordedRequest req = server.takeRequest();
        assertEquals("/accounts.getJWT", req.getPath());
    }

    @Test
    void testGetJwtExpired403005() {
        server.enqueue(gigyaResponse(TestFixtures.GIGYA_FIXTURE_PATH + "/error/get_jwt.403005.json"));
        var ex = assertThrows(GigyaException.class, () ->
            client.getJwt(baseUrl, "test-api-key", TestFixtures.TEST_LOGIN_TOKEN));
        assertEquals(403005, ex.getErrorCode());
    }

    @Test
    void testGetJwtUnverified403013() {
        server.enqueue(gigyaResponse(TestFixtures.GIGYA_FIXTURE_PATH + "/error/get_jwt.403013.json"));
        var ex = assertThrows(GigyaException.class, () ->
            client.getJwt(baseUrl, "test-api-key", TestFixtures.TEST_LOGIN_TOKEN));
        assertEquals(403013, ex.getErrorCode());
    }
}
