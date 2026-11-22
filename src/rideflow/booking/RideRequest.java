package rideflow.booking;

import rideflow.model.*;

/** MEMBER 2 STUB: signatures only; no working implementation.  */
public final class RideRequest {
    public RideRequest(Passenger passenger, Location pickup, Location destination, int partySize, VehicleCategory category) {
        // TODO Member 2: Validate and store the passenger, locations, party size (1-6), and requested category.
    }

    public Passenger getPassenger() {
        // TODO Member 2: Return the passenger stored in this request.
        throw new UnsupportedOperationException("Member 2: implement getPassenger");
    }

    public Location getPickup() {
        // TODO Member 2: Return the stored pickup location.
        throw new UnsupportedOperationException("Member 2: implement getPickup");
    }

    public Location getDestination() {
        // TODO Member 2: Return the stored destination location.
        throw new UnsupportedOperationException("Member 2: implement getDestination");
    }

    public int getPartySize() {
        // TODO Member 2: Return the stored number of passengers.
        throw new UnsupportedOperationException("Member 2: implement getPartySize");
    }

    public VehicleCategory getCategory() {
        // TODO Member 2: Return the requested category.
        throw new UnsupportedOperationException("Member 2: implement getCategory");
    }

    public double getTripDistanceKm() {
        // TODO Member 2: Return pickup.distanceTo(destination), using the stored locations.
        throw new UnsupportedOperationException("Member 2: implement getTripDistanceKm");
    }
}
