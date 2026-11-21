package rideflow.dispatch;

import rideflow.booking.RideRequest;
import rideflow.model.Driver;
import java.util.*;
public final class NearestDriverStrategy implements MatchingStrategy {
    @Override public Optional<Driver> chooseDriver(RideRequest request, List<Driver> drivers) {
        return drivers.stream().min(Comparator
            .comparingDouble((Driver d)->d.getLocation().distanceTo(request.getPickup()))
            .thenComparing(Driver::getId));
    }
    @Override public String name() { return "Nearest driver"; }
}
