package rideflow.dispatch;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import rideflow.booking.RideRequest;
import rideflow.model.Driver;

public class BalancedMatchingStrategy implements MatchingStrategy {

    private final double workloadPenaltyKm;
    private final Map<String, Integer> assignmentCounts = new HashMap<>();

    public BalancedMatchingStrategy() {
        this(2.5);
    }

    public BalancedMatchingStrategy(double workloadPenaltyKm) {
        this.workloadPenaltyKm = workloadPenaltyKm;
    }

    @Override
    public Optional<Driver> chooseDriver(RideRequest request, List<Driver> eligible) {
        if (request == null || eligible == null || eligible.isEmpty()) {
            return Optional.empty();
        }

        Optional<Driver> chosen = eligible.stream()
                .min(Comparator.comparingDouble(driver -> calculateScore(driver, request)));

        chosen.ifPresent(driver ->
                assignmentCounts.merge(driver.getId(), 1, Integer::sum)
        );

        return chosen;
    }

    private double calculateScore(Driver driver, RideRequest request) {
        double distance = driver.getLocation().distanceTo(request.getPickup());
        int assignedSoFar = assignmentCounts.getOrDefault(driver.getId(), 0);
        return distance + (assignedSoFar * workloadPenaltyKm);
    }

    public Map<String, Integer> getAssignmentDistribution() {
        return Map.copyOf(assignmentCounts);
    }

    public void resetCounts() {
        assignmentCounts.clear();
    }
}