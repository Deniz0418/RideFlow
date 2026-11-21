package rideflow.booking;

import rideflow.model.*;
import java.util.Objects;
/** Immutable original customer request; separate from mutable Ride status. */
public final class RideRequest {
    private final Passenger passenger;
    private final Location pickup;
    private final Location destination;
    private final int partySize;
    private final VehicleCategory category;
    private final int requestedAtMinute;
    public RideRequest(Passenger passenger, Location pickup, Location destination,
                       int partySize, VehicleCategory category, int requestedAtMinute) {
        this.passenger = Objects.requireNonNull(passenger);
        this.pickup = Objects.requireNonNull(pickup);
        this.destination = Objects.requireNonNull(destination);
        this.category = Objects.requireNonNull(category);
        if (partySize < 1 || partySize > 6) throw new IllegalArgumentException("Party size must be 1-6");
        if (category != VehicleCategory.XL && partySize > 4)
            throw new IllegalArgumentException("Economy/Premium vehicles only have 4 passenger seats; choose XL");
        if (pickup.distanceTo(destination) == 0) throw new IllegalArgumentException("Pickup and destination must differ");
        if (requestedAtMinute < 0) throw new IllegalArgumentException("Time cannot be negative");
        this.partySize = partySize;
        this.requestedAtMinute = requestedAtMinute;
    }
    public Passenger getPassenger() { return passenger; }
    public Location getPickup() { return pickup; }
    public Location getDestination() { return destination; }
    public int getPartySize() { return partySize; }
    public VehicleCategory getCategory() { return category; }
    public int getRequestedAtMinute() { return requestedAtMinute; }
    public double getTripDistanceKm() { return pickup.distanceTo(destination); }
}
