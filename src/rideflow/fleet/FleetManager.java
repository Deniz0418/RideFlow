package rideflow.fleet;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import rideflow.booking.RideRequest;
import rideflow.model.Driver;
import rideflow.model.Location;
import rideflow.model.VehicleCategory;

/** Owns the fleet. All operations run on the single console/simulation thread. */
public final class FleetManager {
    private final UserDirectory users;
    private final Map<String, Driver> drivers = new LinkedHashMap<>();

    public FleetManager(UserDirectory users) { this.users = Objects.requireNonNull(users, "Users"); }

    public void registerDriver(Driver driver) {
        Objects.requireNonNull(driver, "Driver");
        if (drivers.containsKey(driver.getId())) throw new IllegalArgumentException("Duplicate driver ID");
        if (drivers.values().stream().anyMatch(d -> d.getVehicle().getPlate().equals(driver.getVehicle().getPlate())))
            throw new IllegalArgumentException("Vehicle plate is already assigned to a driver");
        users.registerDriver(driver); // Validates account ID before changing the fleet.
        drivers.put(driver.getId(), driver);
    }

    public Driver getDriver(String id) {
        Driver driver = drivers.get(id);
        if (driver == null) throw new IllegalArgumentException("Unknown fleet driver: " + id);
        return driver;
    }
    public List<Driver> listDrivers() { return List.copyOf(drivers.values()); }

    public List<Driver> getEligibleDrivers(RideRequest request) {
        Objects.requireNonNull(request, "Request");
        return getEligibleDrivers(request.getPartySize(), request.getCategory());
    }
    public List<Driver> getEligibleDrivers(int partySize, VehicleCategory category) {
        if (partySize <= 0 || partySize > 6) throw new IllegalArgumentException("Party size must be 1 to 6");
        Objects.requireNonNull(category, "Category");
        return drivers.values().stream().filter(Driver::isAvailable)
                .filter(d -> d.getVehicle().getCategory() == category)
                .filter(d -> d.getVehicle().canCarry(partySize)).toList();
    }

    /** Rechecks availability when reserving; an earlier eligibility list may be stale. */
    public boolean reserveDriver(String driverId, String rideId, int minute) {
        return getDriver(driverId).assign(rideId, minute);
    }
    public boolean releaseDriver(String driverId, String rideId, int minute) {
        return getDriver(driverId).release(rideId, minute, false);
    }
    public boolean completeAssignment(String driverId, String rideId, int minute) {
        return getDriver(driverId).release(rideId, minute, true);
    }
    public void setDriverOnline(String driverId, boolean online, int minute) {
        getDriver(driverId).setOnline(online, minute);
    }
    public void relocateDriver(String driverId, Location location) { getDriver(driverId).relocate(location); }
    public void moveDriver(String driverId, String rideId, Location nextLocation) {
        getDriver(driverId).moveForRide(rideId, nextLocation);
    }
}
