package com.renault.api.kamereon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.api.exception.*;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KamereonResponseError(
    @JsonProperty("errorCode") String errorCode,
    @JsonProperty("errorMessage") String errorMessage
) {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void raiseForErrorCode() {
        String details = resolveErrorDetails();
        throw switch (errorCode) {
            case "err.func.400"               -> new InvalidInputException(errorCode, details);
            case "err.func.403"               -> new AccessDeniedException(errorCode, details);
            case "err.tech.500"               -> new InvalidUpstreamException(errorCode, details);
            case "err.tech.501"               -> new NotSupportedException(errorCode, details);
            case "err.func.wired.notFound"    -> new ResourceNotFoundException(errorCode, details);
            case "err.tech.wired.kamereon-proxy" -> new FailedForwardException(errorCode, details);
            case "err.func.wired.overloaded"  -> new QuotaLimitException(errorCode, details);
            case "err.func.privacy.on"        -> new PrivacyModeOnException(errorCode, details);
            case "err.func.wired.forbidden"   -> new ForbiddenException(errorCode, details);
            case "409001"                     -> new ChargeModeInProgressException(errorCode, details);
            default                           -> new KamereonException(errorCode, details);
        };
    }

    private String resolveErrorDetails() {
        if (errorMessage == null) return "";
        try {
            JsonNode root = MAPPER.readTree(errorMessage);
            JsonNode errors = root.path("errors");
            if (errors.isArray() && !errors.isEmpty()) {
                List<String> parts = new ArrayList<>();
                for (JsonNode err : errors) {
                    List<String> tokens = new ArrayList<>();
                    if (err.hasNonNull("title")) tokens.add(err.get("title").asText());
                    JsonNode src = err.path("source").path("pointer");
                    if (!src.isMissingNode()) tokens.add(src.asText());
                    if (err.hasNonNull("detail")) tokens.add(err.get("detail").asText());
                    if (!tokens.isEmpty()) parts.add(String.join(" ", tokens));
                }
                if (!parts.isEmpty()) return String.join(", ", parts);
            }
        } catch (Exception ignored) {}
        return errorMessage;
    }
}
