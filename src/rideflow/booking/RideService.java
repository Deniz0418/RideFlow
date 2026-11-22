package rideflow.booking;

import java.util.List;
import rideflow.fleet.UserDirectory;
import rideflow.model.*;

/** MEMBER 2 STUB: signatures only; no working implementation.  */
public final class RideService {
    public RideService(UserDirectory users) {
        // TODO Member 2: Store the shared user directory and initialize ride storage/ID generation.
    }

    public Ride requestRide(Passenger passenger, Location pickup, Location destination, int partySize, VehicleCategory category, int minute) {
        // TODO Member 2: Validate registered passenger, party/category compatibility and no existing active ride; create, store and return a REQUESTED ride.
        throw new UnsupportedOperationException("Member 2: implement requestRide");
    }

    public Ride getRide(String id) {
        // TODO Member 2: Find the stored ride; throw IllegalArgumentException for an unknown ID.
        throw new UnsupportedOperationException("Member 2: implement getRide");
    }

    public List<Ride> listRides() {
        // TODO Member 2: Return an unmodifiable snapshot of stored rides in deterministic order.
        throw new UnsupportedOperationException("Member 2: implement listRides");
    }

    public void queueRide(String id, int minute) {
        // TODO Member 2: Find the ride and validate/record its QUEUED transition.
    }

    public void assignDriver(String id, Driver driver, double fare, int minute) {
        // TODO Member 2: Find the ride and record ASSIGNED after Dispatch has successfully reserved the driver through Fleet.
    }

    public void markEnRoute(String id, int minute) {
        // TODO Member 2: Find the ride and validate/record ASSIGNED -> EN_ROUTE.
    }

    public void startRide(String id, int minute) {
        // TODO Member 2: Find the ride and record EN_ROUTE -> IN_PROGRESS with pickup time.
    }

    public void completeRide(String id, int minute) {
        // TODO Member 2: Find the ride and record IN_PROGRESS -> COMPLETED; SimulationEngine handles release, queue retry and payment.
    }

    public void cancelRide(String id, int minute) {
        // TODO Member 2: Validate cancellation before changing state; SimulationEngine handles reservation/queue cleanup.
    }
}
