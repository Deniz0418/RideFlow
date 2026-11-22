package rideflow.model;

import java.util.Objects;

/** FleetManager is the normal entry point for changing driver state. */
public final class Driver extends User {
    private final Vehicle vehicle;
    private Location location;
    private DriverStatus status = DriverStatus.OFFLINE;
    private String assignedRideId;
    private int assignedAt;
    private int lastStateMinute;
    private int busyMinutes;
    private int completedRides;
    private double earnings;

    public Driver(String id, String name, Vehicle vehicle, Location location) {
        this(id, name, "", vehicle, location);
    }
    public Driver(String id, String name, String phone, Vehicle vehicle, Location location) {
        super(id, name, phone);
        this.vehicle = Objects.requireNonNull(vehicle, "Vehicle");
        this.location = Objects.requireNonNull(location, "Location");
    }
    public Vehicle getVehicle() { return vehicle; }
    public Location getLocation() { return location; }
    public DriverStatus getStatus() { return status; }
    public String getAssignedRideId() { return assignedRideId; }
    public int getCompletedRides() { return completedRides; }
    public double getEarnings() { return earnings; }
    public boolean isAvailable() { return status == DriverStatus.AVAILABLE; }

    public void setOnline(boolean online, int minute) {
        checkMinute(minute);
        if (status == DriverStatus.ASSIGNED)
            throw new IllegalStateException("An assigned driver must finish or cancel before changing availability");
        status = online ? DriverStatus.AVAILABLE : DriverStatus.OFFLINE;
        lastStateMinute = minute;
    }

    /** False means already busy or offline; no existing assignment is overwritten. */
    public boolean assign(String rideId, int minute) {
        rideId = User.requireText(rideId, "Ride ID");
        checkMinute(minute);
        if (!isAvailable()) return false;
        assignedRideId = rideId;
        assignedAt = minute;
        lastStateMinute = minute;
        status = DriverStatus.ASSIGNED;
        return true;
    }

    /** The ride ID prevents a stale cancellation from freeing another ride's driver. */
    public boolean release(String rideId, int minute, boolean completed) {
        Objects.requireNonNull(rideId, "Ride ID");
        checkMinute(minute);
        if (status != DriverStatus.ASSIGNED || !rideId.equals(assignedRideId)) return false;
        busyMinutes = Math.addExact(busyMinutes, minute - assignedAt);
        if (completed) completedRides++;
        assignedRideId = null;
        lastStateMinute = minute;
        status = DriverStatus.AVAILABLE;
        return true;
    }

    public void moveForRide(String rideId, Location nextLocation) {
        if (status != DriverStatus.ASSIGNED || !assignedRideId.equals(rideId))
            throw new IllegalStateException("Driver is not reserved for this ride");
        location = Objects.requireNonNull(nextLocation, "Location");
    }

    public void relocate(Location location) {
        if (status == DriverStatus.ASSIGNED)
            throw new IllegalStateException("Cannot manually relocate an assigned driver");
        this.location = Objects.requireNonNull(location, "Location");
    }

    /** Includes pickup travel and passenger travel, including cancelled assignments. */
    public int getBusyMinutes(int now) {
        checkMinute(now);
        return busyMinutes + (status == DriverStatus.ASSIGNED ? now - assignedAt : 0);
    }

    /** Member 4 calls this only after recording a successful, nonduplicate payment. */
    public void addEarnings(double amount) {
        if (!Double.isFinite(amount) || amount < 0 || !Double.isFinite(earnings + amount))
            throw new IllegalArgumentException("Earnings must be finite and nonnegative");
        earnings += amount;
    }

    private void checkMinute(int minute) {
        if (minute < lastStateMinute)
            throw new IllegalArgumentException("Driver time cannot move backwards");
    }
}
