package com.renault.api.kamereon;

import com.renault.api.TestFixtures;
import com.renault.api.exception.*;
import com.renault.api.kamereon.model.KamereonVehicleDataResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Mirrors Python tests/kamereon/test_kamereon_error.py */
class KamereonErrorTest {

    private KamereonVehicleDataResponse loadError(String filename) {
        return TestFixtures.load(
            TestFixtures.KAMEREON_FIXTURE_PATH + "/error/" + filename,
            KamereonVehicleDataResponse.class);
    }

    @Test
    void testAccessDenied() {
        var response = loadError("access_denied.json");
        assertNotNull(response.getErrors());
        var ex = assertThrows(AccessDeniedException.class, response::raiseForErrorCode);
        assertEquals("err.func.403", ex.getErrorCode());
        assertEquals("Access is denied for this resource", ex.getErrorDetails());
    }

    @Test
    void testQuotaLimit() {
        var response = loadError("quota_limit.json");
        assertNotNull(response.getErrors());
        var ex = assertThrows(QuotaLimitException.class, response::raiseForErrorCode);
        assertEquals("err.func.wired.overloaded", ex.getErrorCode());
        assertEquals("You have reached your quota limit", ex.getErrorDetails());
    }

    @Test
    void testInvalidDate() {
        var response = loadError("invalid_date.json");
        var ex = assertThrows(InvalidInputException.class, response::raiseForErrorCode);
        assertEquals("err.func.400", ex.getErrorCode());
        assertEquals("/data/attributes/startDateTime must be a future date", ex.getErrorDetails());
    }

    @Test
    void testInvalidUpstream() {
        var response = loadError("invalid_upstream.json");
        var ex = assertThrows(InvalidUpstreamException.class, response::raiseForErrorCode);
        assertEquals("err.tech.500", ex.getErrorCode());
        assertEquals(
            "Invalid response from the upstream server (The request sent to the GDC is erroneous) ; 502 Bad Gateway",
            ex.getErrorDetails());
    }

    @Test
    void testNotSupported() {
        var response = loadError("not_supported.json");
        var ex = assertThrows(NotSupportedException.class, response::raiseForErrorCode);
        assertEquals("err.tech.501", ex.getErrorCode());
        assertEquals("This feature is not technically supported by this gateway", ex.getErrorDetails());
    }

    @Test
    void testChargeModeInProgress() {
        var response = loadError("charge_mode_inprogress.json");
        var ex = assertThrows(ChargeModeInProgressException.class, response::raiseForErrorCode);
        assertEquals("409001", ex.getErrorCode());
        assertEquals("A remote CHARGE_MODE is already in progress", ex.getErrorDetails());
    }

    @Test
    void testPrivacyOn() {
        var response = loadError("privacy_on.json");
        var ex = assertThrows(PrivacyModeOnException.class, response::raiseForErrorCode);
        assertEquals("err.func.privacy.on", ex.getErrorCode());
        assertEquals("Privacy mode currently ON", ex.getErrorDetails());
    }

    @Test
    void testResourceNotFound() {
        var response = loadError("resource_not_found.json");
        var ex = assertThrows(ResourceNotFoundException.class, response::raiseForErrorCode);
        assertEquals("err.func.wired.notFound", ex.getErrorCode());
        assertEquals("Resource not found", ex.getErrorDetails());
    }

    @Test
    void testFailedForward() {
        var response = loadError("failed_forward.json");
        var ex = assertThrows(FailedForwardException.class, response::raiseForErrorCode);
        assertEquals("err.tech.wired.kamereon-proxy", ex.getErrorCode());
        assertEquals("Failed to forward request to remote service.", ex.getErrorDetails());
    }

    @Test
    void testForbidden() {
        var response = loadError("forbidden.json");
        var ex = assertThrows(ForbiddenException.class, response::raiseForErrorCode);
        assertEquals("err.func.wired.forbidden", ex.getErrorCode());
        assertEquals("The access is forbidden", ex.getErrorDetails());
    }
}
