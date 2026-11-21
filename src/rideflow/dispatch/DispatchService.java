package rideflow.dispatch;

import rideflow.booking.*;
import rideflow.fleet.FleetManager;
import rideflow.finance.PricingService;
import rideflow.model.Driver;
import java.util.*;
/** Orchestrates matching, waiting and driver reservation; one source of assignment. */
public final class DispatchService {
    private final FleetManager fleet;
    private final RideService rides;
    private final PricingService pricing;
    private final WaitingQueue queue = new WaitingQueue();
    private MatchingStrategy strategy;
    public DispatchService(FleetManager fleet, RideService rides, PricingService pricing, MatchingStrategy strategy) {
        this.fleet=Objects.requireNonNull(fleet);
        this.rides=Objects.requireNonNull(rides);
        this.pricing=Objects.requireNonNull(pricing);
        this.strategy=Objects.requireNonNull(strategy);
    }
    public void setStrategy(MatchingStrategy strategy) { this.strategy=Objects.requireNonNull(strategy); }
    public String strategyName() { return strategy.name(); }
    public WaitingQueue waitingQueue() { return queue; }
    public Optional<Driver> dispatchRide(String rideId, int minute) {
        Ride ride=rides.getRide(rideId);
        if (ride.getStatus()!=RideStatus.REQUESTED && ride.getStatus()!=RideStatus.QUEUED)
            throw new IllegalStateException("Only new/queued rides may be dispatched");
        List<Driver> eligible=fleet.getEligibleDrivers(ride.getRequest());
        Optional<Driver> chosen=strategy.chooseDriver(ride.getRequest(),eligible);
        if (chosen.isEmpty()) {
            rides.markQueued(rideId);
            queue.add(rideId);
            return Optional.empty();
        }
        Driver d=chosen.get();
        // Reserve before marking the ride assigned: no driver may serve two rides.
        fleet.reserveDriver(d.getId());
        try {
            double quotedFare=pricing.quote(ride.getRequest(),eligible.size());
            rides.assignDriver(rideId,d,quotedFare,minute);
            rides.driverEnRoute(rideId);
            queue.remove(rideId);
        } catch (RuntimeException e) {
            fleet.releaseDriver(d.getId());
            throw e;
        }
        return Optional.of(d);
    }
    public void retryWaitingQueue(int minute) {
        for (String rideId : queue.snapshot()) {
            Ride ride=rides.getRide(rideId);
            if (ride.getStatus() != RideStatus.QUEUED) { queue.remove(rideId); continue; }
            dispatchRide(rideId,minute);
        }
    }
    public void removeFromQueue(String rideId) { queue.remove(rideId); }
}
