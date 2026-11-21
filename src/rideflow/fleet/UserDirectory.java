package rideflow.fleet;

import rideflow.model.Passenger;
import java.util.*;
/** Registry for passenger accounts. Driver registration lives in FleetManager. */
public final class UserDirectory {
    private final Map<String, Passenger> passengers = new LinkedHashMap<>();
    public void registerPassenger(Passenger passenger) {
        Objects.requireNonNull(passenger);
        if (passengers.putIfAbsent(passenger.getId(), passenger) != null) throw new IllegalArgumentException("Duplicate passenger ID");
    }
    public Passenger getPassenger(String id) {
        Passenger p = passengers.get(id);
        if (p == null) throw new IllegalArgumentException("Unknown passenger: " + id);
        return p;
    }
    public List<Passenger> listPassengers() { return List.copyOf(passengers.values()); }
}
