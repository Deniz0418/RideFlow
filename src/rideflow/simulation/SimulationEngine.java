package rideflow.simulation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import rideflow.booking.Ride;
import rideflow.booking.RideService;
import rideflow.booking.RideStatus;
import rideflow.dispatch.DispatchService;
import rideflow.finance.PaymentMethod;
import rideflow.finance.PaymentService;
import rideflow.fleet.FleetManager;
import rideflow.model.Driver;
import rideflow.model.Location;
import rideflow.model.Passenger;
import rideflow.model.VehicleCategory;

/**
 * Coordinates services on one thread. Booking owns ride transitions, Fleet owns
 * reservations, Dispatch owns matching/queuing, Finance owns payment records.
 */
public final class SimulationEngine {
    private final SimulationClock clock;
    private final JourneySimulator journeys;
    private final FleetManager fleet;
    private final RideService rides;
    private final DispatchService dispatch;
    private final PaymentService payments;
    private final PaymentMethod defaultPaymentMethod;
    private final Map<String, PaymentMethod> paymentMethods = new LinkedHashMap<>();
    private final Map<String, Ride> pendingPayments = new LinkedHashMap<>();
    private final Map<String, String> paymentErrors = new LinkedHashMap<>();

    public SimulationEngine(SimulationClock clock, JourneySimulator journeys, FleetManager fleet,
            RideService rides, DispatchService dispatch, PaymentService payments,
            PaymentMethod defaultPaymentMethod) {
        this.clock = Objects.requireNonNull(clock, "Clock");
        this.journeys = Objects.requireNonNull(journeys, "Journeys");
        this.fleet = Objects.requireNonNull(fleet, "Fleet");
        this.rides = Objects.requireNonNull(rides, "Rides");
        this.dispatch = Objects.requireNonNull(dispatch, "Dispatch");
        this.payments = Objects.requireNonNull(payments, "Payments");
        this.defaultPaymentMethod = Objects.requireNonNull(defaultPaymentMethod, "Default payment method");
    }

    public int now() { return clock.now(); }
    public String timeLabel() { return clock.timeLabel(); }
    public Map<String, String> getPaymentErrors() { return Map.copyOf(paymentErrors); }

    public Ride submitRide(Passenger passenger, Location pickup, Location destination,
            int partySize, VehicleCategory category) {
        return submitRide(passenger, pickup, destination, partySize, category, defaultPaymentMethod);
    }

    public Ride submitRide(Passenger passenger, Location pickup, Location destination,
            int partySize, VehicleCategory category, PaymentMethod paymentMethod) {
        Objects.requireNonNull(paymentMethod, "Payment method");
        // Check both legs can be represented before creating any ride.
        journeys.travelMinutes(pickup, destination);
        for (Driver driver : fleet.getEligibleDrivers(partySize, category))
            journeys.travelMinutes(driver.getLocation(), pickup);
        Ride ride = rides.requestRide(passenger, pickup, destination, partySize, category, clock.now());
        paymentMethods.put(ride.getId(), paymentMethod);
        dispatch.dispatchRide(ride.getId(), clock.now());
        settleCurrentMinute();
        return ride;
    }

    /** Advance every active ride by one minute before retrying the waiting queue. */
    public void advanceMinutes(int minutes) {
        if (minutes < 0 || minutes > Integer.MAX_VALUE - clock.now())
            throw new IllegalArgumentException("Invalid number of minutes");
        settleCurrentMinute();
        for (int i = 0; i < minutes; i++) {
            clock.tick();
            for (Ride ride : rides.listRides()) {
                if (ride.getStatus() == RideStatus.EN_ROUTE) {
                    move(ride, ride.getRequest().getPickup());
                    if (at(ride, ride.getRequest().getPickup())) {
                        rides.startRide(ride.getId(), clock.now());
                        // A zero-distance passenger journey completes at pickup.
                        if (at(ride, ride.getRequest().getDestination())) finish(ride);
                    }
                } else if (ride.getStatus() == RideStatus.IN_PROGRESS) {
                    move(ride, ride.getRequest().getDestination());
                    if (at(ride, ride.getRequest().getDestination())) finish(ride);
                }
            }
            settleCurrentMinute();
        }
    }

    public Ride cancelRide(String rideId) {
        Ride ride = rides.getRide(rideId);
        // Booking validates cancellation before any queue or fleet changes.
        rides.cancelRide(rideId, clock.now());
        dispatch.removeWaitingRide(rideId);
        if (ride.getDriver() != null)
            fleet.releaseDriver(ride.getDriver().getId(), rideId, clock.now());
        paymentMethods.remove(rideId);
        settleCurrentMinute();
        return ride;
    }

    /** Use this from the console so newly available drivers trigger queue retry. */
    public void setDriverOnline(String driverId, boolean online) {
        fleet.setDriverOnline(driverId, online, clock.now());
        settleCurrentMinute();
    }

    /** Call after registering/relocating a driver through FleetManager. */
    public void refreshDispatch() { settleCurrentMinute(); }

    private void move(Ride ride, Location target) {
        Driver driver = ride.getDriver();
        fleet.moveDriver(driver.getId(), ride.getId(), journeys.moveOneMinute(driver.getLocation(), target));
    }

    private boolean at(Ride ride, Location target) { return ride.getDriver().getLocation().equals(target); }

    private void finish(Ride ride) {
        rides.completeRide(ride.getId(), clock.now());
        if (!fleet.completeAssignment(ride.getDriver().getId(), ride.getId(), clock.now()))
            throw new IllegalStateException("Completed ride did not own its driver's reservation");
        pendingPayments.putIfAbsent(ride.getId(), ride);
    }

    /**
     * Immediate events do not consume another minute. The loop also drains a
     * finite queue of zero-distance rides without delaying each by one tick.
     */
    private void settleCurrentMinute() {
        boolean assigned;
        do {
            for (Ride ride : rides.listRides()) {
                if (ride.getStatus() != RideStatus.ASSIGNED) continue;
                Driver driver = ride.getDriver();
                if (!ride.getId().equals(driver.getAssignedRideId()))
                    throw new IllegalStateException("Assignment must reserve the driver first");
                rides.markEnRoute(ride.getId(), clock.now());
                if (at(ride, ride.getRequest().getPickup())) {
                    rides.startRide(ride.getId(), clock.now());
                    if (at(ride, ride.getRequest().getDestination())) finish(ride);
                }
            }
            dispatch.retryWaitingQueue(clock.now());
            assigned = rides.listRides().stream().anyMatch(r -> r.getStatus() == RideStatus.ASSIGNED);
        } while (assigned);
        retryPendingPayments();
    }

    /**
     * Exceptions are retained for retry; an ordinary failed PaymentResult is a
     * recorded financial outcome. PaymentService must deduplicate by ride ID.
     * Drivers have already been released before payment is attempted.
     */
    public void retryPendingPayments() {
        for (Ride ride : List.copyOf(pendingPayments.values())) {
            try {
                payments.processPayment(ride, paymentMethods.getOrDefault(ride.getId(), defaultPaymentMethod), clock.now());
                pendingPayments.remove(ride.getId());
                paymentMethods.remove(ride.getId());
                paymentErrors.remove(ride.getId());
            } catch (RuntimeException exception) {
                paymentErrors.put(ride.getId(), exception.toString());
            }
        }
    }
}
