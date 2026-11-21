package rideflow.fleet;

import rideflow.booking.RideRequest;
import rideflow.model.*;
import java.util.*;
/** One registry owns driver reservation; all dispatchers must use reserveDriver. */
public final class FleetManager {
    private final Map<String,Driver> drivers = new LinkedHashMap<>();
    public void registerDriver(Driver driver) {
        Objects.requireNonNull(driver);
        if (drivers.putIfAbsent(driver.getId(), driver) != null) throw new IllegalArgumentException("Duplicate driver ID");
    }
    public Driver getDriver(String id) {
        Driver d=drivers.get(id);
        if (d == null) throw new IllegalArgumentException("Unknown driver: " + id);
        return d;
    }
    public List<Driver> listDrivers() { return List.copyOf(drivers.values()); }
    public List<Driver> getEligibleDrivers(RideRequest request) {
        List<Driver> eligible = new ArrayList<>();
        for (Driver d : drivers.values()) {
            if (d.getStatus()==DriverStatus.AVAILABLE &&
                d.getVehicle().getCategory()==request.getCategory() &&
                d.getVehicle().getCapacity()>=request.getPartySize()) eligible.add(d);
        }
        return eligible;
    }
    public void reserveDriver(String driverId) { getDriver(driverId).assign(); }
    public void releaseDriver(String driverId) { getDriver(driverId).release(); }
}
