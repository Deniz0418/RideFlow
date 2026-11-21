package rideflow.booking;

import java.util.*;
import rideflow.model.*;
/** The sole registry of rides and source of truth for passenger active-ride checks. */
public final class RideService {
    private final Map<String,Ride> rides = new LinkedHashMap<>();
    private int nextRideNumber = 1;
    public Ride requestRide(Passenger passenger, Location pickup, Location destination,
                            int partySize, VehicleCategory category, int minute) {
        Objects.requireNonNull(passenger);
        for (Ride ride : rides.values()) {
            if (ride.isActive() && ride.getRequest().getPassenger().getId().equals(passenger.getId()))
                throw new IllegalStateException("Passenger already has an active ride");
        }
        Ride ride = new Ride(String.format("R%03d",nextRideNumber++),
                new RideRequest(passenger,pickup,destination,partySize,category,minute));
        rides.put(ride.getId(),ride);
        return ride;
    }
    public Ride getRide(String id) {
        Ride ride=rides.get(id);
        if (ride == null) throw new IllegalArgumentException("Unknown ride: "+id);
        return ride;
    }
    public List<Ride> listRides() { return List.copyOf(rides.values()); }
    public void markQueued(String id) { getRide(id).queue(); }
    public void assignDriver(String id, Driver driver, double fare, int minute) { getRide(id).assign(driver,fare,minute); }
    public void driverEnRoute(String id) { getRide(id).departForPickup(); }
    public void startRide(String id, int minute) { getRide(id).startTrip(minute); }
    public void completeRide(String id, int minute) { getRide(id).complete(minute); }
    public void cancelRide(String id) { getRide(id).cancel(); }
}
