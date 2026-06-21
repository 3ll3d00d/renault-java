package com.renault.harness;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import com.renault.api.RenaultClient;
import com.renault.api.RenaultVehicle;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/// Browser-based harness UI. Same env vars as {@link HarnessCli}.
/// Starts an HTTP server on port 8080; open http://localhost:8080 in a browser.
public class HarnessServer {

    private static final int PORT = 8080;
    private static final ObjectMapper MAPPER = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();

    public static void main(String[] args) throws Exception {
        String user   = require("RENAULT_USER");
        String pass   = require("RENAULT_PASS");
        String locale = env("RENAULT_LOCALE", "fr_FR");
        Path fixtures = Path.of(env("RENAULT_FIXTURE_PATH",
            "api/src/test/resources/fixtures/kamereon/vehicle_data"));

        var interceptor = new LoggingInterceptor();
        var http = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        System.out.println("Authenticating as " + user + " [" + locale + "] ...");
        RenaultClient client = new RenaultClient(http, locale);
        client.login(user, pass);

        var accounts = client.getAccounts();
        if (accounts.isEmpty()) throw new IllegalStateException("No accounts found");
        var vehicles = accounts.get(0).getVehicles();
        if (vehicles.isEmpty()) throw new IllegalStateException("No vehicles found");

        RenaultVehicle vehicle = vehicles.get(0);
        System.out.println("Vehicle: " + vehicle.getVin());

        EndpointRunner runner = new EndpointRunner(vehicle, fixtures);

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", ex -> {
            if (!ex.getRequestMethod().equals("GET")) { ex.sendResponseHeaders(405, -1); return; }
            byte[] page = loadResource("index.html");
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, page.length);
            ex.getResponseBody().write(page);
            ex.getResponseBody().close();
        });

        server.createContext("/api/endpoints", ex -> {
            if (!ex.getRequestMethod().equals("GET")) { ex.sendResponseHeaders(405, -1); return; }
            sendJson(ex, Map.of("endpoints", EndpointRunner.endpoints(), "vin", vehicle.getVin()));
        });

        server.createContext("/api/run/", ex -> {
            if (!ex.getRequestMethod().equals("GET")) { ex.sendResponseHeaders(405, -1); return; }
            String ep = ex.getRequestURI().getPath().replaceFirst("^/api/run/", "");
            try {
                EndpointRunner.Result result = runner.run(ep);
                var exchange = interceptor.last();
                sendJson(ex, Map.of(
                    "endpoint",    result.endpoint(),
                    "liveJson",    result.liveJson(),
                    "fixtureName", result.fixtureName() != null ? result.fixtureName() : "",
                    "fixtureJson", result.fixtureJson() != null ? result.fixtureJson() : "",
                    "http",        exchange != null ? exchange.toString() : "",
                    "diff",        serializeDiff(result.diff())
                ));
            } catch (Exception e) {
                var exchange = interceptor.last();
                sendJson(ex, Map.of(
                    "error",   e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName(),
                    "http",    exchange != null ? exchange.toString() : ""
                ));
            }
        });

        server.start();
        System.out.println("Harness running at http://localhost:" + PORT);
        System.out.println("Press Ctrl-C to stop.");
    }

    private static List<Map<String, String>> serializeDiff(List<StructuralDiff.Entry> diff) {
        return diff.stream().map(e -> Map.of(
            "path",    e.path(),
            "live",    e.liveType(),
            "fixture", e.fixtureType(),
            "status",  e.status().name()
        )).toList();
    }

    private static void sendJson(HttpExchange ex, Object value) throws IOException {
        byte[] body = MAPPER.writeValueAsBytes(value);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(200, body.length);
        ex.getResponseBody().write(body);
        ex.getResponseBody().close();
    }

    private static byte[] loadResource(String name) throws IOException {
        try (InputStream is = HarnessServer.class.getResourceAsStream(name)) {
            if (is == null) throw new IllegalStateException("Resource not found: " + name);
            return is.readAllBytes();
        }
    }

    private static String require(String name) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) throw new IllegalStateException("Env var " + name + " is required");
        return v;
    }

    private static String env(String name, String def) {
        String v = System.getenv(name);
        return (v != null && !v.isBlank()) ? v : def;
    }
}
