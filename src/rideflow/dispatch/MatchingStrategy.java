package rideflow.dispatch;

import rideflow.booking.RideRequest;
import rideflow.model.Driver;
import java.util.*;
/** Member 3 contract: plug in matching algorithms without changing booking. */
public interface MatchingStrategy {
    Optional<Driver> chooseDriver(RideRequest request, List<Driver> eligibleDrivers);
    String name();
}
