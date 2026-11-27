package rideflow.dispatch;

import java.util.List;
import rideflow.booking.Ride;
import rideflow.booking.RideRequest;
import rideflow.booking.RideService;
import rideflow.booking.RideStatus;
import rideflow.fleet.FleetManager;
import rideflow.model.Driver;
import rideflow.model.EconomyVehicle;
import rideflow.model.Location;
import rideflow.model.Passenger;
import rideflow.model.VehicleCategory;
import rideflow.model.XLVehicle;

/**
 * MEMBER 3 - Driver Matching and Dispatch
 *
 * Tasks:
 * - Find eligible drivers using capacity, category, and availability.
 * - Implement nearest-driver matching.
 * - Implement and compare a second matching strategy.
 * - Assign drivers to requests without double-booking vehicles.
 * - Maintain a waiting queue when no driver is available.
 * - Retry queued requests when drivers become free.
 * - Define measurable outcomes for algorithm comparisons.
 *
 * Coordinate driver information with Member 1 and ride requests/statuses
 * with Member 2. Use interfaces/strategies where they improve the design.
 *
 * This placeholder class may be renamed or deleted by the member.
 */
public final class DispatchModule {

    private DispatchModule() {
        // Utility / Factory class
    }

    /**
     * Creates a DispatchService configured with the default NearestDriverStrategy.
     */
    public static DispatchService createDefaultDispatchService(FleetManager fleet, RideService rides) {
        return new DispatchService(fleet, rides, new NearestDriverStrategy(), null);
    }

    /**
     * Creates a DispatchService configured with the workload-aware BalancedMatchingStrategy.
     */
    public static DispatchService createBalancedDispatchService(FleetManager fleet, RideService rides) {
        return new DispatchService(fleet, rides, new BalancedMatchingStrategy(), null);
    }

    /**
     * Standalone test runner to verify Member 3 functionality independently.
     */
    public static void main(String[] args) {
        System.out.println("=== RideFlow Member 3: Dispatch Module Verification ===");

        testNearestDriverAndCapacityFiltering();
        testWaitingQueueAndRetry();
        testBalancedStrategyDistribution();

        System.out.println("=== All Member 3 verification checks completed ===");
    }

    private static void testNearestDriverAndCapacityFiltering() {
        System.out.println("\n[Test 1] Nearest Driver & Vehicle Capacity Filtering");

        FleetManager fleet = new FleetManager();
        RideService rides = new RideService();
        DispatchService dispatch = createDefaultDispatchService(fleet, rides);

        // Register 3 drivers: 2 Economy (near & far), 1 XL (far)
        Driver d1 = new Driver("D1", "Alice", new EconomyVehicle("ECO-101"), new Location(1.0, 1.0));
        Driver d2 = new Driver("D2", "Bob", new EconomyVehicle("ECO-102"), new Location(9.0, 9.0));
        Driver d3 = new Driver("D3", "Charlie", new XLVehicle("XL-201"), new Location(8.0, 8.0));

        fleet.registerDriver(d1);
        fleet.registerDriver(d2);
        fleet.registerDriver(d3);

        // Request 1: Economy ride for 2 passengers near (2, 2) -> should match D1 (Alice)
        Passenger p1 = new Passenger("P1", "John");
        Ride r1 = rides.requestRide(p1, new Location(2.0, 2.0), new Location(6.0, 6.0), 2, VehicleCategory.ECONOMY, 10);
        dispatch.dispatchRide(r1.getId(), 10);

        System.out.println("Ride 1 Status: " + r1.getStatus()
                + " | Assigned Driver: " + (r1.getDriver() != null ? r1.getDriver().getId() : "NONE")
                + " (Expected: D1)");

        // Request 2: 6 passengers requiring XL -> must skip D2 (Economy) and pick D3 (Charlie)
        Passenger p2 = new Passenger("P2", "Sarah");
        Ride r2 = rides.requestRide(p2, new Location(3.0, 3.0), new Location(10.0, 10.0), 6, VehicleCategory.XL, 11);
        dispatch.dispatchRide(r2.getId(), 11);

        System.out.println("Ride 2 Status: " + r2.getStatus()
                + " | Assigned Driver: " + (r2.getDriver() != null ? r2.getDriver().getId() : "NONE")
                + " (Expected: D3)");
    }

    private static void testWaitingQueueAndRetry() {
        System.out.println("\n[Test 2] High Demand Waiting Queue, Cancellation & Retry");

        FleetManager fleet = new FleetManager();
        RideService rides = new RideService();
        DispatchService dispatch = createDefaultDispatchService(fleet, rides);

        Driver d1 = new Driver("D1", "Alice", new EconomyVehicle("ECO-101"), new Location(0.0, 0.0));
        fleet.registerDriver(d1);

        Passenger p1 = new Passenger("P1", "User1");
        Passenger p2 = new Passenger("P2", "User2");
        Passenger p3 = new Passenger("P3", "User3");

        Ride r1 = rides.requestRide(p1, new Location(1.0, 1.0), new Location(5.0, 5.0), 1, VehicleCategory.ECONOMY, 1);
        Ride r2 = rides.requestRide(p2, new Location(2.0, 2.0), new Location(6.0, 6.0), 1, VehicleCategory.ECONOMY, 2);
        Ride r3 = rides.requestRide(p3, new Location(3.0, 3.0), new Location(7.0, 7.0), 1, VehicleCategory.ECONOMY, 3);

        dispatch.dispatchRide(r1.getId(), 1); // Assigned to D1
        dispatch.dispatchRide(r2.getId(), 2); // Queued
        dispatch.dispatchRide(r3.getId(), 3); // Queued

        System.out.println("Waiting Queue after 3 requests: " + dispatch.getWaitingRideIds()
                + " (Expected: [" + r2.getId() + ", " + r3.getId() + "])");

        // Cancel r2 while queued
        r2.setStatus(RideStatus.CANCELLED);
        dispatch.removeWaitingRide(r2.getId());

        // Complete r1 and release D1
        rides.completeRide(r1.getId(), 15);
        fleet.releaseDriver(d1.getId());

        // Retry queue -> should skip cancelled r2 and assign r3 to D1
        dispatch.retryWaitingQueue(15);

        System.out.println("Ride 3 Status after retry: " + r3.getStatus()
                + " | Driver: " + (r3.getDriver() != null ? r3.getDriver().getId() : "NONE")
                + " (Expected: ASSIGNED / D1)");
        System.out.println("Remaining Waiting Queue: " + dispatch.getWaitingRideIds() + " (Expected: [])");
    }

    private static void testBalancedStrategyDistribution() {
        System.out.println("\n[Test 3] BalancedMatchingStrategy Workload Distribution");

        FleetManager fleet = new FleetManager();
        RideService rides = new RideService();
        BalancedMatchingStrategy balancedStrategy = new BalancedMatchingStrategy(3.0);
        DispatchService dispatch = new DispatchService(fleet, rides, balancedStrategy, null);

        // D1 is slightly closer (1km away) than D2 (2km away)
        Driver d1 = new Driver("D1", "Alice", new EconomyVehicle("ECO-101"), new Location(1.0, 0.0));
        Driver d2 = new Driver("D2", "Bob", new EconomyVehicle("ECO-102"), new Location(2.0, 0.0));
        fleet.registerDriver(d1);
        fleet.registerDriver(d2);

        // First ride goes to D1 (closer)
        Ride r1 = rides.requestRide(new Passenger("P1", "U1"), new Location(0.0, 0.0), new Location(4.0, 4.0), 1, VehicleCategory.ECONOMY, 1);
        dispatch.dispatchRide(r1.getId(), 1);
        rides.completeRide(r1.getId(), 10);
        fleet.releaseDriver(d1.getId());

        // Second ride at same spot: D1 now has 1 trip (+3.0km penalty -> score 4.0) vs D2 (0 trips -> score 2.0)
        // Balanced strategy should pick D2 instead of overloading D1
        Ride r2 = rides.requestRide(new Passenger("P2", "U2"), new Location(0.0, 0.0), new Location(4.0, 4.0), 1, VehicleCategory.ECONOMY, 12);
        dispatch.dispatchRide(r2.getId(), 12);

        System.out.println("Ride 2 Assigned Driver under Balanced Strategy: "
                + (r2.getDriver() != null ? r2.getDriver().getId() : "NONE")
                + " (Expected: D2)");
        System.out.println("Distribution counts: " + balancedStrategy.getAssignmentDistribution());
    }
}
