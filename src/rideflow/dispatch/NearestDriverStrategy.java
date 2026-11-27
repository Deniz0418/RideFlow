package rideflow.dispatch;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import rideflow.booking.RideRequest;
import rideflow.model.Driver;

public class NearestDriverStrategy implements MatchingStrategy {

    @Override
    public Optional<Driver> chooseDriver(RideRequest request, List<Driver> eligible) {
        if (request == null || eligible == null || eligible.isEmpty()) {
            return Optional.empty();
        }

        return eligible.stream()
                .min(Comparator.comparingDouble(
                        driver -> driver.getLocation().distanceTo(request.getPickup())
                ));
    }
}