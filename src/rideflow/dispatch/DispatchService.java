package rideflow.dispatch;

import java.util.List;
import rideflow.booking.RideService;
import rideflow.fleet.FleetManager;

/** MEMBER 3 STUB: signatures only; no working implementation.  */
public final class DispatchService {
    public DispatchService(FleetManager fleet, RideService rides) {
        // TODO Member 3: Store the shared services and initialize the queue. Add matching/pricing dependencies when implementing your full module.
    }

    public void dispatchRide(String id, int minute) {
        // TODO Member 3: Get eligible drivers, use MatchingStrategy, obtain a quote, reserve through Fleet, and assign through Booking. Queue when unmatched; roll back reservation if assignment fails.
    }

    public void retryWaitingQueue(int minute) {
        // TODO Member 3: Check a snapshot of waiting rides once, discard terminal/cancelled IDs, and retry matching. Do not block or loop waiting for future drivers.
    }

    public void removeWaitingRide(String id) {
        // TODO Member 3: Remove this ride from the waiting queue, especially after cancellation.
    }

    public List<String> getWaitingRideIds() {
        // TODO Member 3: Return an unmodifiable ordered snapshot of waiting ride IDs.
        throw new UnsupportedOperationException("Member 3: implement getWaitingRideIds");
    }
}
