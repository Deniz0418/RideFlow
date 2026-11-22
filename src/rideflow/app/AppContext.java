package rideflow.app;

import rideflow.booking.RideService;
import rideflow.dispatch.DispatchService;
import rideflow.finance.*;
import rideflow.fleet.*;
import rideflow.simulation.*;

/** MEMBER 2 STUB: signatures only; no working implementation.  */
public final class AppContext {
    // TODO Member 2: initialize ONE shared instance of each service in the constructor.
    // These fields are intentionally uninitialized; no dependency wiring is implemented.
    public UserDirectory users;
    public FleetManager fleet;
    public RideService rides;
    public DispatchService dispatch;
    public PaymentService payments;
    public SimulationClock clock;
    public SimulationEngine simulation;

    public AppContext() {
        // TODO Member 2: Construct and connect the shared services, supplying an actual simulated default PaymentMethod from Member 4.
    }

    public AppContext(PaymentMethod method) {
        // TODO Member 2: Wire the shared services using the supplied payment method, with the same instances passed to Dispatch and SimulationEngine.
    }

    public static AppContext withDemoData() {
        // TODO Member 2: Return a wired context populated with repeatable registered users/drivers; do not duplicate shared objects.
        throw new UnsupportedOperationException("Member 2: implement withDemoData");
    }
}
