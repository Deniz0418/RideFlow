package rideflow.model;

import java.util.Objects;
public final class Driver extends User {
    private final Vehicle vehicle;
    private Location location;
    private DriverStatus status = DriverStatus.AVAILABLE;
    private int completedRides;
    private double totalEarnings;
    public Driver(String id, String name, Vehicle vehicle, Location location) {
        super(id,name);
        this.vehicle = Objects.requireNonNull(vehicle);
        this.location = Objects.requireNonNull(location);
    }
    public Vehicle getVehicle() { return vehicle; }
    public Location getLocation() { return location; }
    public DriverStatus getStatus() { return status; }
    public int getCompletedRides() { return completedRides; }
    public double getTotalEarnings() { return totalEarnings; }
    public void setLocation(Location location) { this.location = Objects.requireNonNull(location); }
    public void assign() {
        if (status != DriverStatus.AVAILABLE) throw new IllegalStateException("Driver is not available: " + getId());
        status = DriverStatus.ASSIGNED;
    }
    public void release() {
        if (status != DriverStatus.ASSIGNED) throw new IllegalStateException("Driver has no active assignment");
        status = DriverStatus.AVAILABLE;
    }
    public void setOffline() {
        if (status == DriverStatus.ASSIGNED) throw new IllegalStateException("Cannot go offline during an assignment");
        status = DriverStatus.OFFLINE;
    }
    public void setOnline() {
        if (status != DriverStatus.OFFLINE) throw new IllegalStateException("Driver is already online");
        status = DriverStatus.AVAILABLE;
    }
    public void recordCompletedRide() { completedRides++; }
    public void creditEarnings(double amount) {
        if (!Double.isFinite(amount) || amount < 0) throw new IllegalArgumentException("Invalid earnings amount");
        totalEarnings += amount;
    }
}
