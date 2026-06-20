package com.renault.api.gigya;

import com.renault.api.TestFixtures;
import com.renault.api.gigya.model.GigyaAccountInfoResponse;
import com.renault.api.gigya.model.GigyaJwtResponse;
import com.renault.api.gigya.model.GigyaLoginResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/gigya/test_gigya_models.py */
class GigyaModelsTest {

    @Test
    void testLoginResponse() {
        var response = TestFixtures.load(TestFixtures.GIGYA_FIXTURE_PATH + "/login.json", GigyaLoginResponse.class);
        assertDoesNotThrow(response::raiseForErrorCode);
        assertEquals(TestFixtures.TEST_LOGIN_TOKEN, response.getSessionCookie());
    }

    @Test
    void testGetAccountInfoResponse() {
        var response = TestFixtures.load(TestFixtures.GIGYA_FIXTURE_PATH + "/get_account_info.json", GigyaAccountInfoResponse.class);
        assertDoesNotThrow(response::raiseForErrorCode);
        assertEquals(TestFixtures.TEST_PERSON_ID, response.getPersonId());
    }

    @Test
    void testGetJwtResponse() {
        var response = TestFixtures.load(TestFixtures.GIGYA_FIXTURE_PATH + "/get_jwt.json", GigyaJwtResponse.class);
        assertDoesNotThrow(response::raiseForErrorCode);
        assertEquals("sample-jwt-token", response.getJwt());
    }
}
