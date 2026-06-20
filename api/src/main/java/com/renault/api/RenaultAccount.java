package com.renault.api;

import com.renault.api.kamereon.model.KamereonVehiclesLink;

import java.util.List;

/** Proxy to a Renault account. */
public class RenaultAccount {
    private final String accountId;
    private final RenaultSession session;

    RenaultAccount(String accountId, RenaultSession session) {
        this.accountId = accountId;
        this.session = session;
    }

    public String getAccountId() { return accountId; }

    /** Returns all vehicles linked to this account. */
    public List<RenaultVehicle> getVehicles() {
        var response = session.getAccountVehicles(accountId);
        List<KamereonVehiclesLink> links = response.getVehicleLinks();
        if (links == null) return List.of();
        return links.stream()
            .map(link -> new RenaultVehicle(accountId, link.vin(), session, link.vehicleDetails()))
            .toList();
    }

    /** Returns the vehicle with the given VIN. */
    public RenaultVehicle getVehicle(String vin) {
        return new RenaultVehicle(accountId, vin, session, null);
    }
}
