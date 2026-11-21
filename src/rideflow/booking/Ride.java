package rideflow.booking;

import rideflow.model.Driver;
import java.util.Objects;
/** Owns ride lifecycle; do not modify its status directly from other modules. */
public final class Ride {
    private final String id;
    private final RideRequest request;
    private RideStatus status = RideStatus.REQUESTED;
    private Driver driver;
    private double quotedFare;
    private int assignedAtMinute = -1;
    private int startedAtMinute = -1;
    private int completedAtMinute = -1;
    public Ride(String id, RideRequest request) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Ride ID required");
        this.id = id;
        this.request = Objects.requireNonNull(request);
    }
    public String getId() { return id; }
    public RideRequest getRequest() { return request; }
    public RideStatus getStatus() { return status; }
    public Driver getDriver() { return driver; }
    public double getQuotedFare() { return quotedFare; }
    public int getAssignedAtMinute() { return assignedAtMinute; }
    public int getStartedAtMinute() { return startedAtMinute; }
    public int getCompletedAtMinute() { return completedAtMinute; }
    public boolean isActive() { return status != RideStatus.COMPLETED && status != RideStatus.CANCELLED; }
    public void queue() {
        ensure(RideStatus.REQUESTED, RideStatus.QUEUED);
        status = RideStatus.QUEUED;
    }
    public void assign(Driver driver, double fare, int now) {
        ensure(RideStatus.REQUESTED, RideStatus.QUEUED);
        if (!Double.isFinite(fare) || fare < 0 || now < request.getRequestedAtMinute()) throw new IllegalArgumentException("Invalid fare or time");
        this.driver = Objects.requireNonNull(driver);
        this.quotedFare = fare;
        this.assignedAtMinute = now;
        status = RideStatus.ASSIGNED;
    }
    public void departForPickup() { ensure(RideStatus.ASSIGNED); status = RideStatus.EN_ROUTE; }
    public void startTrip(int now) {
        ensure(RideStatus.EN_ROUTE);
        if (now < assignedAtMinute) throw new IllegalArgumentException("Invalid start time");
        startedAtMinute = now;
        status = RideStatus.IN_PROGRESS;
    }
    public void complete(int now) {
        ensure(RideStatus.IN_PROGRESS);
        if (now < startedAtMinute) throw new IllegalArgumentException("Invalid completion time");
        completedAtMinute = now;
        status = RideStatus.COMPLETED;
    }
    public void cancel() {
        if (status == RideStatus.IN_PROGRESS || !isActive()) throw new IllegalStateException("Ride cannot be cancelled in status " + status);
        status = RideStatus.CANCELLED;
    }
    private void ensure(RideStatus... allowed) {
        for (RideStatus s : allowed) if (status == s) return;
        throw new IllegalStateException("Invalid transition from " + status);
    }
    @Override public String toString() {
        return id + " | " + request.getPassenger().getName() + " | " + status +
                " | driver=" + (driver == null ? "none" : driver.getId()) +
                " | fare=" + String.format(java.util.Locale.ROOT,"%.2f", quotedFare);
    }
}
