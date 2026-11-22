# What each file does

**Current state:** Member 1 is implemented. Other members' files are now declaration-only TODO stubs. The full application is not runnable until those methods are implemented.

## Your code: `src/rideflow/model`

| File | What it does | Why it exists |
|---|---|---|
| `User.java` | Abstract parent with a permanent ID and editable name/phone. Rejects blank IDs and names. Phone is optional. | Avoids duplicating common account fields in Passenger and Driver. |
| `Passenger.java` | A concrete User representing someone booking a ride. | Booking owns the passenger's active ride and history, so those records are not copied into this class. |
| `Driver.java` | A concrete User with one vehicle, current position, status, reserved ride ID, completed-ride count, busy minutes, and earnings total. Guards assignment, release, relocation, and online/offline changes. | Provides one consistent driver state for dispatch, simulation, and analytics. |
| `Vehicle.java` | Abstract vehicle parent. Stores a normalized plate and declares `getCapacity()` and `getCategory()`. `canCarry()` checks party size. | Demonstrates abstraction and polymorphism: Fleet uses Vehicle without checking concrete subclass names. |
| `EconomyVehicle.java` | Economy category, four passenger seats. | Implements one concrete vehicle type. |
| `PremiumVehicle.java` | Premium category, four passenger seats. | Implements another concrete vehicle type. Fare rules belong to Member 4. |
| `XLVehicle.java` | XL category, six passenger seats. | Allows groups of five or six. |
| `Location.java` | Immutable `(x, y)` kilometre coordinates. Calculates straight-line distance and rejects nonfinite coordinates. No intermediate positions are calculated. | Gives everyone the same distance calculations. Immutability prevents accidental coordinate changes through shared references. |
| `VehicleCategory.java` | Enum containing `ECONOMY`, `PREMIUM`, and `XL`. | Replaces error-prone category strings in the program. |
| `DriverStatus.java` | Enum containing `AVAILABLE`, `ASSIGNED`, and `OFFLINE`. | Limits availability state to the agreed three values. |

## Your code: `src/rideflow/fleet`

| File | What it does |
|---|---|
| `UserDirectory.java` | Registers passenger and driver accounts in one directory; prevents duplicate IDs across both types; performs typed lookups; returns unmodifiable list snapshots. Register drivers through FleetManager so the account and fleet stay connected. |
| `FleetManager.java` | Registers drivers and prevents duplicate vehicle plates. Supplies available drivers with matching category and sufficient capacity, reserves/releases them, controls online/offline state, and updates their locations. Rechecks status when reserving so a stale eligibility list cannot double-book a driver. |

The important safety rule is **release the driver only if the ride ID matches the current reservation**. If R001 is cancelled after a driver has moved to R002, releasing R001 returns false and leaves R002 intact. FleetManager is used on a single application thread; it is not advertised as a multithreaded service.

## Your code: `src/rideflow/simulation`

Continuous driver movement has been removed. The clock and timed pickup/completion remain so cancellation rules, passenger waiting time, resource release, and queue retries still work.

| File | What it does |
|---|---|
| `SimulationClock.java` | Stores elapsed integer minutes. `now()` reads time, `tick()` advances one minute, and `timeLabel()` displays it. Minute zero is 10:00; labels include a day number after midnight. |
| `JourneySimulator.java` | Uses a configurable fixed speed, default 30 km/h. Calculates travel duration only; it does not calculate movement or intermediate positions. |
| `SimulationEngine.java` | Coordinates the other services. Submits requests to Booking/Dispatch, schedules pickup/completion events, updates position only when an event occurs, releases drivers through Fleet, retries the queue, and invokes payment after completion. Handles cancellation and online changes through the same coordination path. |

## How one ride progresses

Example: driver at `(-1, 0)`, pickup `(0, 0)`, destination `(1, 0)`, speed 30 km/h.

| Elapsed minute | Driver location | Ride status | What happened |
|---|---|---|---|
| 0 | `(-1, 0)` | EN_ROUTE | Dispatch reserved the driver; the engine began pickup travel. |
| 1 | `(-1, 0)` | EN_ROUTE | Waiting for the pickup event at minute 2; position is not interpolated. |
| 2 | `(0, 0)` | IN_PROGRESS | Driver reached pickup; Booking recorded pickup/start time. |
| 3 | `(0, 0)` | IN_PROGRESS | Waiting for completion at minute 4; position remains the last event location. |
| 4 | `(1, 0)` | COMPLETED | Booking completed the ride; Fleet released the driver; payment hook ran. |

Each ride has its own scheduled next event. Two two-minute trips can both finish after two simulated minutes. Processing the event checks sequentially in Java does not make the journeys run one after another.

At each simulated minute the engine handles due events, then retries queued requests. Newly assigned rides calculate their own pickup time from that timestamp. A zero-distance pickup can start the passenger trip immediately.

## Rules chosen for this implementation

These make the UML's unspecified details concrete and should be reviewed during team integration.

- Coordinates are kilometres. Distances are Euclidean. Speed defaults to 30 km/h.
- Vehicle category matches exactly. Premium/XL are not automatic upgrades for Economy.
- Economy/Premium capacity is four; XL capacity is six. Member 2 must reject a five/six-person request unless XL is selected.
- Drivers begin OFFLINE. Register them through FleetManager, then bring them online through SimulationEngine.
- A passenger has one active ride at most; Member 2 must implement this rule.
- Cancellation is permitted in REQUESTED, QUEUED, ASSIGNED, and EN_ROUTE. It is refused at/after pickup.
- Pickup begins the passenger trip immediately. There is no additional boarding delay.
- Each travel leg uses whole minutes, rounded up. Completion is scheduled as pickup time plus passenger-trip duration.
- A zero-distance leg consumes zero minutes. A zero-distance trip with nonzero pickup travel still requires the pickup leg.
- Positions change only at pickup and completion. Cancelling before pickup leaves the driver at their last recorded location (the location at assignment); no partial travel is represented. Completion sets the destination. Both release the reservation before queue retry.
- Busy minutes include pickup and passenger travel, including time spent on a later-cancelled assignment. Completed-ride counts exclude cancellations.
- Finance owns payment records and successful earnings credit. Member 1 stores an earnings total but never automatically treats completion as payment success.
- A normal failed payment is recorded as a failure. A thrown payment exception is exposed through `getPaymentErrors()` and retained for retry. Finance must deduplicate by ride ID, including after uncertain outcomes.
- Nothing sleeps or uses background threads. Identical scenario inputs and service strategies give repeatable results.

## Declaration-only teammate files: `support-src`

All methods below are unimplemented. Each method has its own TODO describing the task. Void methods/constructors have empty bodies apart from comments; methods with a return type throw UnsupportedOperationException. No matching, storage, queueing, lifecycle, charging, menus, or wiring is implemented here.

| File | Owner | What they must implement |
|---|---|---|
| `booking/RideStatus.java` | Member 2 | Enum values are declared; implement the isActive helper. |
| `booking/RideRequest.java` | Member 2 | Store/validate request fields, implement getters and trip distance. |
| `booking/Ride.java` | Member 2 | Store ride data, guard transitions, record event times, implement getters and summary. |
| `booking/RideService.java` | Member 2 | Store/find rides, validate passenger requests and active-ride conflicts, perform lifecycle transitions. |
| `dispatch/WaitingQueue.java` | Member 3 | Store unique waiting IDs in FIFO order; add, remove and snapshot them. |
| `dispatch/DispatchService.java` | Member 3 | Wire matching/pricing, reserve through Fleet, assign through Booking, queue/retry unmatched rides and remove cancellations. |
| `finance/PaymentMethod.java` | Member 4 | Interface only; implement charge in cash/card/wallet classes. |
| `finance/PaymentResult.java` | Member 4 | Data record only; payment implementations must produce truthful success/failure outcomes. Java generates record accessors. |
| `finance/PaymentService.java` | Member 4 | Charge completed rides, record results/transactions, prevent duplicates, and credit successful revenue/earnings. |
| `app/AppContext.java` | Member 2 | Initialize and connect the same shared service instances; optionally provide repeatable demo data. Fields are currently uninitialized. |
| `app/ConsoleMenu.java` | Member 2 | Implement menus, input handling, registration/reporting and calls to services. |
| `app/Main.java` | Member 2 | Construct the shared context and start the console. Currently does nothing. |

These files are integration contracts, not completed work. Preserve existing parameter and return types when implementing them, or coordinate any necessary signature changes with the group.

## Tests, build and documentation

| File | Purpose |
|---|---|
| `test/rideflow/tests/FleetModelTest.java` | Ten runnable checks for Member 1's independent models, fleet, clock and travel-duration calculations. |
| `test/rideflow/tests/Member1Test.java` | Original 25 integration scenarios, retained unchanged for after the other members implement their classes. Deferred in this version. |
| `build.xml` | Compile defaults and independent tests; run/demo targets are retained for later integration. |
| `nbproject/project.xml` | Existing NetBeans freeform source-root configuration. |
| `run.bat` / `run.sh` | Compile by default; test runs independent checks; integration attempts the deferred scenarios. |
| `README.md` | Stub conventions, replacement and compile instructions. |
| `docs/INTEGRATION_CONTRACTS.md` | Proposed signatures and responsibility boundaries. |
| `docs/VERIFICATION.md` | Actual checks and limitations for this version. |
| `docs/UML_MAIN.svg` / `docs/UML_MAIN_SOURCE.puml` | Unchanged master diagram and editable source. Older optional-design comments predate the final proposal. |

## Suggested reading order

Read User, Passenger, Vehicle and subclasses, Location, Driver, UserDirectory, FleetManager, SimulationClock, JourneySimulator, and finally SimulationEngine. Read the enums when encountered. User/Vehicle show inheritance and polymorphism; private state and guarded operations show encapsulation; SimulationEngine shows collaboration across the four members' services once their methods are implemented.
