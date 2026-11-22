package rideflow.booking;

import rideflow.model.Driver;

/** MEMBER 2 STUB: signatures only; no working implementation.  */
public final class Ride {
    public Ride(String id, RideRequest request, int minute) {
        // TODO Member 2: Store identity/request and requested time. Initialize status to REQUESTED and unset timestamps to -1.
    }

    public String getId() {
        // TODO Member 2: Return the permanent ride ID.
        throw new UnsupportedOperationException("Member 2: implement getId");
    }

    public RideRequest getRequest() {
        // TODO Member 2: Return the request belonging to this ride.
        throw new UnsupportedOperationException("Member 2: implement getRequest");
    }

    public RideStatus getStatus() {
        // TODO Member 2: Return the current lifecycle status.
        throw new UnsupportedOperationException("Member 2: implement getStatus");
    }

    public Driver getDriver() {
        // TODO Member 2: Return the assigned driver, or null before assignment.
        throw new UnsupportedOperationException("Member 2: implement getDriver");
    }

    public double getQuotedFare() {
        // TODO Member 2: Return the fare fixed at assignment/confirmation.
        throw new UnsupportedOperationException("Member 2: implement getQuotedFare");
    }

    public int getRequestedAt() {
        // TODO Member 2: Return elapsed simulated minutes when requested.
        throw new UnsupportedOperationException("Member 2: implement getRequestedAt");
    }

    public int getAssignedAt() {
        // TODO Member 2: Return assignment minute, or -1 when not assigned.
        throw new UnsupportedOperationException("Member 2: implement getAssignedAt");
    }

    public int getPickupAt() {
        // TODO Member 2: Return pickup minute, or -1 before pickup.
        throw new UnsupportedOperationException("Member 2: implement getPickupAt");
    }

    public int getCompletedAt() {
        // TODO Member 2: Return completion minute, or -1 before completion.
        throw new UnsupportedOperationException("Member 2: implement getCompletedAt");
    }

    public int getCancelledAt() {
        // TODO Member 2: Return cancellation minute, or -1 when not cancelled.
        throw new UnsupportedOperationException("Member 2: implement getCancelledAt");
    }

    void queue(int minute) {
        // TODO Member 2: Allow REQUESTED/QUEUED -> QUEUED, rejecting backwards event times.
    }

    public void assign(Driver driver, double fare, int minute) {
        // TODO Member 2: Validate REQUESTED/QUEUED -> ASSIGNED; store driver, valid fare, and assignment time.
    }

    void markEnRoute(int minute) {
        // TODO Member 2: Allow ASSIGNED -> EN_ROUTE and validate event time.
    }

    public void startTrip(int minute) {
        // TODO Member 2: Allow EN_ROUTE -> IN_PROGRESS and record pickup time.
    }

    public void complete(int minute) {
        // TODO Member 2: Allow IN_PROGRESS -> COMPLETED and record completion time.
    }

    public void cancel(int minute) {
        // TODO Member 2: Allow cancellation only before pickup; record CANCELLED and cancellation time.
    }

    @Override public String toString() {
        // TODO Member 2: Return a readable summary including ID, passenger, status, and driver.
        throw new UnsupportedOperationException("Member 2: implement toString");
    }
}
