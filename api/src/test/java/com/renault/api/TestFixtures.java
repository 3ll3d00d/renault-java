package com.renault.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Shared helpers mirroring the Python test suite's fixtures.py and const.py. */
public final class TestFixtures {
    public static final String GIGYA_FIXTURE_PATH = "fixtures/gigya";
    public static final String KAMEREON_FIXTURE_PATH = "fixtures/kamereon";

    public static final String TEST_ACCOUNT_ID = "account-id-1";
    public static final String TEST_COUNTRY = "FR";
    public static final String TEST_LOCALE = "fr_FR";
    public static final String TEST_LOGIN_TOKEN = "sample-cookie-value";
    public static final String TEST_PERSON_ID = "person-id-1";
    public static final String TEST_USERNAME = "test@example.com";
    public static final String TEST_PASSWORD = "test_password";
    public static final String TEST_VIN = "VF1AAAAA555777999";

    public static final ObjectMapper MAPPER = JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .changeDefaultVisibility(v -> v.withVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY))
        .build();

    private TestFixtures() {}

    public static String load(String resourcePath) {
        try (InputStream is = TestFixtures.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalArgumentException("Fixture not found: " + resourcePath);
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load fixture: " + resourcePath, e);
        }
    }

    public static <T> T load(String resourcePath, Class<T> type) {
        try {
            return MAPPER.readValue(load(resourcePath), type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse %s as %s".formatted(resourcePath, type.getSimpleName()), e);
        }
    }
}
