package com.renault.harness;

import com.renault.api.RenaultClient;
import com.renault.api.RenaultVehicle;
import okhttp3.OkHttpClient;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/// Interactive CLI harness. Credentials via env vars:
/// `RENAULT_USER`, `RENAULT_PASS`, `RENAULT_LOCALE` (default `fr_FR`).
/// Fixture path via `RENAULT_FIXTURE_PATH` (default `api/src/test/resources/fixtures/kamereon/vehicle_data`).
public class HarnessCli {

    public static void main(String[] args) throws Exception {
        String user    = require("RENAULT_USER");
        String pass    = require("RENAULT_PASS");
        String locale  = env("RENAULT_LOCALE", "fr_FR");
        Path fixtures  = Path.of(env("RENAULT_FIXTURE_PATH",
            "api/src/test/resources/fixtures/kamereon/vehicle_data"));

        var interceptor = new LoggingInterceptor();
        var http = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        System.out.println("Authenticating as " + user + " [" + locale + "] ...");
        RenaultClient client = new RenaultClient(http, locale);
        client.login(user, pass);
        System.out.println("Login token: " + client.getLoginToken());

        System.out.println("Fetching accounts ...");
        var accounts = client.getAccounts();
        if (accounts.isEmpty()) { System.err.println("No accounts found."); return; }

        System.out.println("Fetching vehicles ...");
        var vehicles = accounts.get(0).getVehicles();
        if (vehicles.isEmpty()) { System.err.println("No vehicles found."); return; }

        RenaultVehicle vehicle = vehicles.get(0);
        System.out.println("Vehicle: " + vehicle.getVin());

        EndpointRunner runner = new EndpointRunner(vehicle, fixtures);
        List<String> endpoints = EndpointRunner.endpoints();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Available endpoints ===");
            for (int i = 0; i < endpoints.size(); i++) {
                System.out.printf("  %2d) %s%n", i + 1, endpoints.get(i));
            }
            System.out.println("   0) Exit");
            System.out.print("Select: ");

            String line = scanner.nextLine().trim();
            if (line.equals("0") || line.equalsIgnoreCase("q")) break;

            int choice;
            try { choice = Integer.parseInt(line) - 1; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); continue; }
            if (choice < 0 || choice >= endpoints.size()) { System.out.println("Out of range."); continue; }

            String ep = endpoints.get(choice);
            System.out.println("\n--- " + ep + " ---");
            try {
                EndpointRunner.Result result = runner.run(ep);
                var exchange = interceptor.last();
                if (exchange != null) {
                    System.out.println("HTTP:  " + exchange);
                }
                System.out.println("\nLIVE RESPONSE:");
                System.out.println(result.liveJson());

                if (result.fixtureName() != null) {
                    System.out.println("\nFIXTURE (" + result.fixtureName() + ") — STRUCTURAL DIFF:");
                    printDiff(result.diff());
                } else {
                    System.out.println("\n(no fixture found for this endpoint)");
                }
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
                var exchange = interceptor.last();
                if (exchange != null) System.err.println("Last HTTP: " + exchange);
            }
        }

        client.close();
        System.out.println("Done.");
    }

    private static void printDiff(List<StructuralDiff.Entry> diff) {
        if (diff.isEmpty()) { System.out.println("  (no fixture attributes to compare)"); return; }
        for (var entry : diff) {
            String mark = switch (entry.status()) {
                case OK -> "  ✓";
                case MISSING_IN_LIVE -> "  ✗ MISSING";
                case EXTRA_IN_LIVE   -> "  + EXTRA  ";
                case TYPE_MISMATCH   -> "  ! MISMATCH";
            };
            System.out.printf("%s  %-45s  live=%-12s  fixture=%s%n",
                mark, entry.path(), entry.liveType(), entry.fixtureType());
        }
        long issues = diff.stream().filter(e -> e.status() != StructuralDiff.Status.OK).count();
        System.out.println("  → " + (issues == 0 ? "All fields match structurally." : issues + " issue(s)."));
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
