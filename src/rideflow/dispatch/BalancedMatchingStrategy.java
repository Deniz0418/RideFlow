package rideflow.dispatch;

import rideflow.booking.RideRequest;
import rideflow.model.Driver;
import java.util.*;
/** Starter heuristic; Member 3 should refine/benchmark this strategy. */
public final class BalancedMatchingStrategy implements MatchingStrategy {
    @Override public Optional<Driver> chooseDriver(RideRequest request, List<Driver> drivers) {
        return drivers.stream().min(Comparator
            .comparingDouble((Driver d)->d.getLocation().distanceTo(request.getPickup())
                + 0.5 * d.getCompletedRides())
            .thenComparing(Driver::getId));
    }
    @Override public String name() { return "Balanced assignments (starter heuristic)"; }
}
