# Proposed shared Java contracts

**Current skeleton state:** All concrete teammate methods are now unimplemented TODO stubs. Void methods do nothing; methods with return values throw UnsupportedOperationException. Enums/interface/record declarations remain for type compatibility. Descriptions below state required future behaviour, not existing implementations.

These signatures let Member 1 compile against the supplied skeletons; they do not make the full application runnable. The master UML fixed class names and relationships but left many parameter/return types open. Review these types with the other members before combining independently written code. No method signature below is presented as already approved by the whole team.

Use the **same object instances** for each registered driver, passenger, and shared service. Do not create a second copy of driver or ride state in Dispatch or Simulation.

## Package names

| Package | Owner |
|---|---|
| `rideflow.model` | Member 1: User, Passenger, Driver, vehicles, Location, enums |
| `rideflow.fleet` | Member 1: UserDirectory, FleetManager |
| `rideflow.simulation` | Member 1: clock, journey calculations, engine |
| `rideflow.booking` | Member 2 |
| `rideflow.app` | Member 2 |
| `rideflow.dispatch` | Member 3 |
| `rideflow.finance` | Member 4 |

## Member 1's public entry points

```java
Passenger(String id, String name)
Passenger(String id, String name, String phone)
Driver(String id, String name, Vehicle vehicle, Location location)
Driver(String id, String name, String phone, Vehicle vehicle, Location location)
EconomyVehicle(String plate)
PremiumVehicle(String plate)
XLVehicle(String plate)
Location(double x, double y)

// UserDirectory
void registerPassenger(Passenger passenger)
void registerDriver(Driver driver) // Normally called by FleetManager, not the menu.
Passenger getPassenger(String id)
Driver getDriver(String id)
Optional<User> findUser(String id)
List<Passenger> listPassengers()
List<Driver> listDrivers()

// FleetManager(UserDirectory users)
void registerDriver(Driver driver)
Driver getDriver(String driverId)
List<Driver> listDrivers()
List<Driver> getEligibleDrivers(RideRequest request)
List<Driver> getEligibleDrivers(int partySize, VehicleCategory category)
boolean reserveDriver(String driverId, String rideId, int minute)
boolean releaseDriver(String driverId, String rideId, int minute)
boolean completeAssignment(String driverId, String rideId, int minute)
void setDriverOnline(String driverId, boolean online, int minute)
void relocateDriver(String driverId, Location location)
void moveDriver(String driverId, String rideId, Location nextLocation) // Pickup/completion event only.

// SimulationClock
int now()
void tick()
String timeLabel()

// JourneySimulator() or JourneySimulator(double speedKmPerHour)
double getSpeedKmPerHour()
int travelMinutes(Location from, Location to)

// SimulationEngine
Ride submitRide(Passenger passenger, Location pickup, Location destination,
                int partySize, VehicleCategory category)
Ride submitRide(Passenger passenger, Location pickup, Location destination,
                int partySize, VehicleCategory category, PaymentMethod method)
Ride cancelRide(String rideId)
void advanceMinutes(int minutes)
void setDriverOnline(String driverId, boolean online)
void refreshDispatch()
int now()
String timeLabel()
Map<String, String> getPaymentErrors()
void retryPendingPayments()
```

SimulationEngine's constructor takes, in order: `SimulationClock`, `JourneySimulator`, `FleetManager`, `RideService`, `DispatchService`, `PaymentService`, and a default `PaymentMethod`.

`reserveDriver` returns false when a driver is busy/offline. `releaseDriver` and `completeAssignment` return false if the given ride does not own that reservation. `completeAssignment` increments completed workload; ordinary release does not. The ride ID parameter deliberately refines the UML's shorthand `reserveDriver(id)` and `releaseDriver(id)` to guard stale events.

Unknown IDs/invalid values throw `IllegalArgumentException`. Forbidden state changes throw `IllegalStateException`. Null object references are rejected. List/map results are unmodifiable snapshots, although the contained Driver/Ride objects remain the shared live objects.

## Methods required from Member 2

```java
// RideRequest
Passenger getPassenger()
Location getPickup()
Location getDestination()
int getPartySize()
VehicleCategory getCategory()
double getTripDistanceKm()

// Ride
String getId()
RideRequest getRequest()
RideStatus getStatus()
Driver getDriver() // Null before assignment.
double getQuotedFare()

// RideService
Ride requestRide(Passenger passenger, Location pickup, Location destination,
                 int partySize, VehicleCategory category, int minute)
Ride getRide(String rideId)
List<Ride> listRides()
void queueRide(String rideId, int minute)
void assignDriver(String rideId, Driver driver, double fare, int minute)
void markEnRoute(String rideId, int minute)
void startRide(String rideId, int minute)
void completeRide(String rideId, int minute)
void cancelRide(String rideId, int minute)
```

RideStatus values: `REQUESTED`, `QUEUED`, `ASSIGNED`, `EN_ROUTE`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`.

Booking owns validation, ride creation and valid transitions. Cancellation validation must happen before Fleet/queue mutation. These RideService methods do **not** independently release drivers, retry dispatch, or process payment in this integration: SimulationEngine performs those side effects. Otherwise the same event would be processed twice.

The console should call `simulation.submitRide`, `simulation.cancelRide`, `simulation.setDriverOnline`, and `simulation.advanceMinutes`. User registration uses UserDirectory/FleetManager. After adding or manually relocating a driver, call `simulation.refreshDispatch()` to reconsider waiting requests.

The Member 2 implementation must record `requestedAt`, `assignedAt`, `pickupAt`, `completedAt`, and `cancelledAt` as elapsed minutes, using `-1` when an event has not occurred. Average pickup wait is `pickupAt - requestedAt`; journey duration is `completedAt - pickupAt`. Include only rides where the relevant events actually occurred.

## Methods required from Member 3

```java
void dispatchRide(String rideId, int minute)
void retryWaitingQueue(int minute)
void removeWaitingRide(String rideId)
```

Dispatch selects a candidate from Fleet's eligible drivers, quotes via Member 4's eventual PricingService, calls `fleet.reserveDriver(driverId, rideId, minute)`, then calls Booking's `assignDriver`. Roll back the reservation if assignment fails. Do not assign if reservation returns false.

If no driver can be reserved, queue the ride once. Retry must ignore/remove cancelled and terminal rides. Retry returns after checking the current queue; it must not spin waiting for future availability. Matching returns an ASSIGNED ride; the engine handles EN_ROUTE/pickup/travel.

Dispatch is now entirely unimplemented. Member 3 must implement it using MatchingStrategy, FleetManager, RideService and PricingService; no first-eligible fallback or zero-fare logic remains.

## Methods required from Member 4

```java
// PaymentMethod
PaymentResult charge(double amount)

// PaymentResult
boolean success()
String message()

// PaymentService
PaymentResult processPayment(Ride ride, PaymentMethod method, int minute)
```

The engine ignores the return value of `processPayment`, so a real implementation may use another return type or `void` if desired without changing that call. It must still record financial failure and deduplicate attempts by ride ID. Only a thrown exception is retained for engine retry; an ordinary failed PaymentResult is a completed failed attempt, not an engine retry request.

Payment runs **after** Booking marks completion and Fleet releases the driver. A payment failure must not occupy the driver or prevent waiting rides from being assigned. A payment exception is exposed in the engine's error map; pending calls are retried on subsequent engine activity or an explicit `retryPendingPayments()` call.

For Finance/Analytics, Driver exposes `getCompletedRides()`, `getBusyMinutes(int now)`, `getEarnings()`, and `addEarnings(double amount)`. `addEarnings` validates an amount but does not deduplicate it: the real PaymentService must credit earnings only once per successful transaction. Completed workload and paid earnings are separate measures. `getBusyMinutes(now)` includes cancelled assignments and the elapsed portion of any current assignment; divide by an explicitly chosen observation window when calculating utilization.

## Simplified simulation behaviour

There is no continuous driver movement. JourneySimulator calculates each leg's duration once. The engine schedules a pickup event, then a completion event. Until an event occurs, the recorded driver location remains at the last event location. At pickup it becomes the pickup point; at completion it becomes the destination. A cancellation before pickup leaves it at the assignment location.

The old `Location.moveTowards(...)` and `JourneySimulator.moveOneMinute(...)` methods have been removed. Existing booking/dispatch/payment signatures and the UML class hierarchy remain the same. FleetManager's `moveDriver` method is now used only to set the location when an event occurs.

## Integration boundary

All operations assume a single Java application thread. The simulation represents several simultaneous journeys through ticks; it does not require threads, locks, real GPS, or real bank payments. Keep the existing UML class hierarchy; integrate the real booking, matching, pricing, finance, and analytics services against these boundaries or adjust the call sites together.
