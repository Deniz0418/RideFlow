package rideflow.dispatch;

import java.util.List;
import java.util.Optional;
import rideflow.booking.RideRequest;
import rideflow.model.Driver;

public interface MatchingStrategy {
    /**
     * Selects the best driver from a pre-filtered list of eligible drivers.
     *
     * @param request  The passenger's ride request
     * @param eligible List of available drivers matching capacity and vehicle category
     * @return An Optional containing the chosen Driver, or empty if none is suitable
     */
    Optional<Driver> chooseDriver(RideRequest request, List<Driver> eligible);
}