package rideflow.dispatch;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import rideflow.booking.Ride;
import rideflow.booking.RideRequest;
import rideflow.booking.RideService;
import rideflow.booking.RideStatus;
import rideflow.finance.PricingService;
import rideflow.fleet.FleetManager;
import rideflow.model.Driver;

public final class DispatchService {

    private final FleetManager fleet;
    private final RideService rides;
    private final WaitingQueue waitingQueue;

    private MatchingStrategy strategy;
    private PricingService pricing;

    // Performance metrics for algorithm comparisons
    private int totalDispatchedCount = 0;
    private long totalWaitMinutes = 0;

    // Required 2-arg constructor matching leader's stub & AppContext
    public DispatchService(FleetManager fleet, RideService rides) {
        this(fleet, rides, new NearestDriverStrategy(), null);
    }

    public DispatchService(FleetManager fleet, RideService rides, MatchingStrategy strategy, PricingService pricing) {
        this.fleet = Objects.requireNonNull(fleet, "fleet must not be null");
        this.rides = Objects.requireNonNull(rides, "rides must not be null");
        this.strategy = (strategy != null) ? strategy : new NearestDriverStrategy();
        this.pricing = pricing;
        this.waitingQueue = new WaitingQueue();
    }

    public void setStrategy(MatchingStrategy strategy) {
        if (strategy != null) {
            this.strategy = strategy;
        }
    }

    public MatchingStrategy getStrategy() {
        return strategy;
    }

    public void setPricingService(PricingService pricing) {
        this.pricing = pricing;
    }

    public void dispatchRide(String id, int minute) {
        if (id == null) {
            return;
        }

        Ride ride = rides.getRide(id);
        if (ride == null) {
            waitingQueue.remove(id);
            return;
        }

        RideStatus status = ride.getStatus();
        if (status != RideStatus.REQUESTED && status != RideStatus.QUEUED) {
            waitingQueue.remove(id);
            return;
        }

        RideRequest request = ride.getRequest();
        List<Driver> eligible = fleet.getEligibleDrivers(request);
        Optional<Driver> chosenOpt = strategy.chooseDriver(request, eligible);

        if (chosenOpt.isPresent()) {
            Driver driver = chosenOpt.get();
            boolean reserved = fleet.reserveDriver(driver.getId());

            if (reserved) {
                try {
                    double fare = (pricing != null)
                            ? pricing.quote(request)
                            : calculateFallbackFare(request);

                    rides.assignDriver(id, driver, fare, minute);
                    waitingQueue.remove(id);

                    totalDispatchedCount++;
                    totalWaitMinutes += Math.max(0, minute - request.getRequestedMinute());
                    return;
                } catch (RuntimeException ex) {
                    // Roll back reservation if Booking assignment fails
                    fleet.releaseDriver(driver.getId());
                    throw ex;
                }
            }
        }

        // Unmatched: ensure ride is marked QUEUED and added to FIFO waiting queue
        if (ride.getStatus() == RideStatus.REQUESTED) {
            ride.setStatus(RideStatus.QUEUED);
        }
        waitingQueue.add(id);
    }

    public void retryWaitingQueue(int minute) {
        List<String> snapshot = waitingQueue.snapshot();
        for (String id : snapshot) {
            Ride ride = rides.getRide(id);
            if (ride == null || isTerminalOrActive(ride.getStatus())) {
                waitingQueue.remove(id);
                continue;
            }
            dispatchRide(id, minute);
        }
    }

    public void removeWaitingRide(String id) {
        waitingQueue.remove(id);
    }

    public List<String> getWaitingRideIds() {
        return waitingQueue.snapshot();
    }

    public double getAverageWaitMinutes() {
        return totalDispatchedCount == 0 ? 0.0 : (double) totalWaitMinutes / totalDispatchedCount;
    }

    public int getTotalDispatchedCount() {
        return totalDispatchedCount;
    }

    private boolean isTerminalOrActive(RideStatus status) {
        return status == RideStatus.ASSIGNED
                || status == RideStatus.EN_ROUTE
                || status == RideStatus.IN_PROGRESS
                || status == RideStatus.COMPLETED
                || status == RideStatus.CANCELLED;
    }

    private double calculateFallbackFare(RideRequest request) {
        // Used only if PricingService has not yet been wired in
        return 5.0 + (request.getTripDistanceKm() * 2.0);
    }
}
