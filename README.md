# RideFlow — Team Starter

**Java 21 · Apache NetBeans / Ant · Console Application · 4 members**

RideFlow is a proposed ride-hailing management and simulation project. The repository is **only a team starter**, not a working booking system. It includes a runnable entry point and four empty placeholder modules with task descriptions. No ride booking, dispatch, simulation, pricing, or payment logic has been implemented.

## How to open and run

1. Open this `RideFlow` directory as a project in Apache NetBeans (**File → Open Project**).
2. Use JDK 21 for the project.
3. Run the project (F6). The console should print a welcome message and say modules are not yet implemented.
4. You may also run `ant run` from this directory if Ant and JDK 21 are installed.

## Member responsibilities

| Member | Starting Java file | Owns |
|---|---|---|
| 1 | `fleet/FleetModule.java` | Users, vehicles, fleet, coordinates, simulated time, vehicle movement |
| 2 | `booking/BookingModule.java` | Ride requests and statuses, cancellations, histories, console UI |
| 3 | `dispatch/DispatchModule.java` | Matching algorithms, driver assignment, waiting queue, comparison |
| 4 | `finance/FinanceModule.java` | Pricing, simulated payments, transactions, analytics |

Each file contains only a comment describing that member's job. Members **should design and add their own classes**, and can delete/rename the placeholder once their code is organized.

## One proposed UML class diagram

See **`docs/UML_MAIN.svg`**. The editable source is **`docs/UML_MAIN_SOURCE.puml`**. These describe a **possible future architecture**, not the five classes currently implemented. The group should review and revise the proposed classes, relationships and methods together. Neither the UML nor its suggested names are compulsory.

## Agree on interfaces first

Before coding independently, discuss how these interactions will work:

- Booking passes a ride request to Dispatch.
- Dispatch obtains driver availability/capacity from Fleet and reserves a driver.
- Fleet/Simulation tells Booking when pickup and completion occur.
- Booking triggers Finance to quote/process completed rides.
- Analytics gathers data from the other modules.

Agree on your basic models (e.g., `RideRequest`, `Driver`, `Location`), method signatures and data types before implementing interconnected services. Review the four-way task division if the workload becomes uneven.

## Git workflow

Create one feature branch per member (for example, `feature/fleet-simulation`, `feature/ride-booking`, `feature/dispatch`, and `feature/finance-analytics`). Commit working changes and merge them by pull request after compiling and reviewing interfaces. Keep the shared `main` branch buildable.

## Scope reminder

The goal is an intelligent console simulation, not real GPS, a graphical application, or real payment processing. Useful milestones are: (1) one end-to-end ride, (2) multiple drivers and concurrent requests, (3) waiting queue and simulated trips, and (4) fares, reports and algorithm comparison.
