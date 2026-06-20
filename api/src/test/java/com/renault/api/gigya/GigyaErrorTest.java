package com.renault.api.gigya;

import com.renault.api.TestFixtures;
import com.renault.api.exception.GigyaException;
import com.renault.api.exception.InvalidCredentialsException;
import com.renault.api.gigya.model.GigyaJwtResponse;
import com.renault.api.gigya.model.GigyaLoginResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/gigya/test_gigya_error.py */
class GigyaErrorTest {

    @Test
    void testJwt403005UnauthorizedUser() {
        var response = TestFixtures.load(
            TestFixtures.GIGYA_FIXTURE_PATH + "/error/get_jwt.403005.json", GigyaJwtResponse.class);
        var ex = assertThrows(GigyaException.class, response::raiseForErrorCode);
        assertEquals(403005, ex.getErrorCode());
        assertEquals("Unauthorized user", ex.getErrorDetails());
    }

    @Test
    void testJwt403013UnverifiedUser() {
        var response = TestFixtures.load(
            TestFixtures.GIGYA_FIXTURE_PATH + "/error/get_jwt.403013.json", GigyaJwtResponse.class);
        var ex = assertThrows(GigyaException.class, response::raiseForErrorCode);
        assertEquals(403013, ex.getErrorCode());
        assertEquals("Unverified user", ex.getErrorDetails());
    }

    @Test
    void testLogin403042InvalidCredentials() {
        var response = TestFixtures.load(
            TestFixtures.GIGYA_FIXTURE_PATH + "/error/login.403042.json", GigyaLoginResponse.class);
        var ex = assertThrows(InvalidCredentialsException.class, response::raiseForErrorCode);
        assertEquals(403042, ex.getErrorCode());
        assertEquals("invalid loginID or password", ex.getErrorDetails());
    }
}
