package rideflow.booking;

/** MEMBER 2: shared enum declarations needed by Member 1. */
public enum RideStatus {
    REQUESTED, QUEUED, ASSIGNED, EN_ROUTE, IN_PROGRESS, COMPLETED, CANCELLED;

    public boolean isActive() {
        // TODO Member 2: return true for all statuses except COMPLETED and CANCELLED.
        throw new UnsupportedOperationException("Member 2: implement isActive");
    }
}
