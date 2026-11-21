package rideflow.simulation;

import rideflow.booking.*;
import rideflow.dispatch.DispatchService;
import rideflow.fleet.FleetManager;
import rideflow.finance.*;
import rideflow.model.*;
import java.util.*;
/**
 * Starter simulation, intentionally simple: drivers teleport on scheduled arrival.
 * Member 1 extends this to progressive movement; Member 3 improves queue behavior.
 */
public final class SimulationEngine {
    private final SimulationClock clock;
    private final RideService rides;
    private final FleetManager fleet;
    private final DispatchService dispatch;
    private final PaymentService payments;
    private final JourneySimulator journey;
    private final Map<String,Integer> pickupDue=new HashMap<>();
    private final Map<String,Integer> dropoffDue=new HashMap<>();
    public SimulationEngine(SimulationClock clock, RideService rides, FleetManager fleet,
                            DispatchService dispatch, PaymentService payments, JourneySimulator journey) {
        this.clock=clock; this.rides=rides; this.fleet=fleet;
        this.dispatch=dispatch; this.payments=payments; this.journey=journey;
    }
    public int now() { return clock.now(); }
    public String timeLabel() { return clock.formatted(); }
    public Ride submitRide(Passenger passenger, Location pickup, Location destination,
                           int partySize, VehicleCategory category) {
        Ride r=rides.requestRide(passenger,pickup,destination,partySize,category,clock.now());
        dispatch.dispatchRide(r.getId(),clock.now());
        return r;
    }
    /** Only call this method (not Ride.cancel directly): it releases the driver and queue ID. */
    public void cancelRide(String rideId) {
        Ride r=rides.getRide(rideId);
        rides.cancelRide(rideId);
        dispatch.removeFromQueue(rideId);
        pickupDue.remove(rideId);
        dropoffDue.remove(rideId);
        if (r.getDriver()!=null) fleet.releaseDriver(r.getDriver().getId());
        dispatch.retryWaitingQueue(clock.now());
    }
    public void advanceMinutes(int minutes) {
        if (minutes < 1 || minutes > 10000) throw new IllegalArgumentException("Advance 1-10000 minutes");
        for (int i=0;i<minutes;i++) advanceOneMinute();
    }
    private void advanceOneMinute() {
        clock.tick();
        for (Ride ride:rides.listRides()) {
            if (ride.getStatus()==RideStatus.EN_ROUTE) {
                int due=pickupDue.computeIfAbsent(ride.getId(),k->
                    ride.getAssignedAtMinute() + journey.travelMinutes(ride.getDriver().getLocation(),ride.getRequest().getPickup()));
                if (clock.now() >= due) {
                    journey.arriveAtPickup(ride);
                    rides.startRide(ride.getId(),clock.now());
                    dropoffDue.put(ride.getId(),clock.now() + journey.travelMinutes(ride.getRequest().getPickup(),ride.getRequest().getDestination()));
                    pickupDue.remove(ride.getId());
                }
            } else if (ride.getStatus()==RideStatus.IN_PROGRESS) {
                Integer due=dropoffDue.get(ride.getId());
                if (due!=null && clock.now()>=due) {
                    journey.arriveAtDestination(ride);
                    rides.completeRide(ride.getId(),clock.now());
                    ride.getDriver().recordCompletedRide();
                    fleet.releaseDriver(ride.getDriver().getId());
                    payments.processPayment(ride,new CashPayment(),clock.now());
                    dropoffDue.remove(ride.getId());
                    dispatch.retryWaitingQueue(clock.now());
                }
            }
        }
    }
}
